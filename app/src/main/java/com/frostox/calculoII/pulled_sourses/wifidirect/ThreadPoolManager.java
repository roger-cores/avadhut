package com.frostox.calculoII.pulled_sourses.wifidirect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadPoolManager extends HandlerThread {
	private final ServerSocket serverSocket;
	private final ExecutorService pool;
	private final AppNetService netService;
	private final static String TAG = "ServiceThread";
	
	public ThreadPoolManager(AppNetService service, int port, int poolSize)
			throws IOException {
		super(TAG, Process.THREAD_PRIORITY_FOREGROUND);
		assert (poolSize > 0);
		this.netService = service;
		serverSocket = new ServerSocket(port);
		pool = Executors.newFixedThreadPool(poolSize);
		Log.d(TAG, "constructor ...");
	}
	public Handler getHandler() {
		return handler;
	}
	
	private boolean isServiceRun = true;
	final void setServiceRun(boolean isRun) {
		this.isServiceRun = isRun; 
	}
	final boolean isServiceRun() {
		return isServiceRun; 
	}
// // NOTE: ����ע�����ݵ�д���Ǵ���ģ�handler������ServiceThread����start֮ǰ��looperΪnull��
//	private Handler handler = new Handler() {
//	@Override
//	public void handleMessage(Message msg) {
//		switch (msg.what) {
//		case ConfigInfo.MSG_SERVICE_POOL_START:
//			while (isServiceRun()) {
//				try {
//					Log.d("ServicePool", "run ...");
//					Socket sock = serverSocket.accept();
//					pool.execute(new HandleAcceptSocket(netService, sock));
//				} catch (IOException ex) {
//					pool.shutdown();
//				}			
//			}
//			
//		}
//	}
//};
	private Handler handler = null;
	static private class ServiceThreadHandler extends Handler {
		private ThreadPoolManager sThread;
		ServiceThreadHandler(ThreadPoolManager service) {
			super(service.getLooper());
			this.sThread = service;
		}
		// �����̶��˿ڵȴ�����
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ConfigInfo.MSG_SERVICE_POOL_START:
				while (sThread.isServiceRun()) {
					try {
						Log.d(TAG, "run ...");
						Socket sock = sThread.serverSocket.accept();
						sThread.pool.execute(new HandleAcceptSocket(sThread.netService, sock));
					} catch (IOException ex) {
						Log.e(TAG, "IOException ex:" + ex);
						sThread.pool.shutdown();
						break;
					}			
				}
				break;
			// default:
			}
			super.handleMessage(msg);
		}
	}
	
	public void init() {
		Log.d(this.getName(), "init - isAlive " + isAlive());
		setServiceRun(true);
		if (!this.isAlive()) {
			this.start();
			handler = new ServiceThreadHandler(this);
		}
		Message msg = new Message();
		msg.what = ConfigInfo.MSG_SERVICE_POOL_START;
		getHandler().sendMessage(msg);
	}

	public void uninit() {
		Log.d(this.getName(), "uninit");
		setServiceRun(false);
		//close();
	}
	
	public void execute (Runnable command) {
		pool.execute(command);
	}
	
//	public void open() {		
//	}
//	public void close() {		
//	}
	
	public void destory() {
		setServiceRun(false);
		shutdownAndAwaitTermination();
		this.quit();
	}
	
	private void shutdownAndAwaitTermination() {// ExecutorService pool
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
			if (!serverSocket.isClosed()) {
				serverSocket.close();
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
			Log.e(TAG, "InterruptedException ie:", ie);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "IOException e:", e);
		}
	}
}

class HandleAcceptSocket implements Runnable {
	private final Socket socket;
	private final AppNetService netService;
	private static final ReentrantLock lockRecvFile = new ReentrantLock();

	HandleAcceptSocket(AppNetService service, Socket socket) {
		this.netService = service;
		this.socket = socket;
	}
	
	public void closeSocket() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				Log.e(this.getClass().getName(), "exception e:" + e);
				e.printStackTrace();
			}
		}
	}
	public void run() {
		// read and service request on socket
		SocketAddress sockAddr = socket.getRemoteSocketAddress();
		Log.d(this.getClass().getName(), "sockAddr:" + sockAddr);
		
		try {
			InputStream ins = socket.getInputStream();
			int iCommand = ins.read();
			Log.d(this.getClass().getName(), "Run iCommand:" + iCommand);
			if (iCommand == ConfigInfo.COMMAND_ID_SEND_PEER_INFO) {
				netService.handleRecvPeerInfo(ins);
			} else if (iCommand == ConfigInfo.COMMAND_ID_SEND_FILE) {
				lockRecvFile.lock();
				try {
					netService.setRemoteSockAddress(sockAddr);
					netService.handleRecvFile(ins);
				} finally {
					lockRecvFile.unlock();
				}
			} else if (iCommand == ConfigInfo.COMMAND_ID_REQUEST_SEND_FILE) {
				netService.handleRecvFileInfo(ins);
			} else if (iCommand == ConfigInfo.COMMAND_ID_RESPONSE_SEND_FILE) {
				// TODO ...
			} else if (iCommand == ConfigInfo.COMMAND_ID_BROADCAST_PEER_LIST) {
				netService.handleRecvPeerList(ins);
			}  else if (iCommand == ConfigInfo.COMMAND_ID_SEND_STRING) {
				// TODO ...
			}
			
			ins.close();
		} catch (IOException e) {
			Log.e(this.getClass().getName(), e.getMessage());
			return;
		}
	}
}
