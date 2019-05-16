package com.shizhenbao.util;

import android.util.Log;

import com.shizhenbao.pop.SystemSet;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class FTP {
	public int temp;//判断选择哪个服务器，0为默认，1为设置
	/**
	 * 服务器名.
	 */
	public String hostName;

	/**
	 * 端口号
	 */
	public int serverPort;

	/**
	 * 用户名.
	 */
	public String userName;

	/**
	 * 密码.
	 */
	public String password;

	/**
	 * FTP连接.
	 */
	private FTPClient ftpClient;

	public FTP() {
		this.ftpClient = new FTPClient();
		ftpClient.setControlEncoding("GBK");

	}

	// -------------------------------------------------------文件上传方法------------------------------------------------

	/**
	 * 上传单个文件.
	 *
	 *
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 *            监听器
	 * @throws IOException
	 */
	public void uploadSingleFile(File singleFile, String remotePath
	) throws IOException {

		// 上传之前初始化
		this.uploadBeforeOperate(remotePath);

		boolean flag;
		flag = uploadingSingle(singleFile);
		if (flag) {
			if(connectResult != null){
				connectResult.getUploadResult(true);
			}
		} else {
			if(connectResult != null){
				connectResult.getUploadResult(false);
			}
		}

		// 上传完成之后关闭连接
		this.uploadAfterOperate();
	}

	/**
	 * 上传多个文件.
	 *
	 *
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 *            监听器
	 * @throws IOException
	 */
	public void uploadMultiFile(LinkedList<File> fileList, String remotePath) throws IOException {
		ftpClient.setControlEncoding("GBK");
		// 上传之前初始化
		this.uploadBeforeOperate(remotePath);
		boolean flag = false;
		for (File singleFile : fileList) {
			flag = uploadingSingle(singleFile);
		}
		if (flag) {
			if(connectResult != null){
				connectResult.getUploadResult(true);
			}
		} else {
			if(connectResult != null){
				connectResult.getUploadResult(false);
			}
		}
		// 上传完成之后关闭连接
		this.uploadAfterOperate();
	}

	/**
	 * 上传单个文件.
	 *
	 * @param localFile
	 *            本地文件
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingSingle(File localFile){
		ftpClient.setControlEncoding("GBK");
		boolean flag = true;
		// 带有进度的方式
		try {
			BufferedInputStream buffIn = new BufferedInputStream(
					new FileInputStream(localFile));
			ProgressInputStream progressInput = new ProgressInputStream(buffIn,
					localFile);
			flag = ftpClient.storeFile(localFile.getName(), progressInput);
			buffIn.close();
		}catch (Exception e){
			flag=false;
		}
		return flag;
	}

	/**
	 * 上传文件之前初始化相关参数
	 *
	 * @param remotePath
	 *            FTP目录
	 *            监听器
	 * @throws IOException
	 */
	private void uploadBeforeOperate(String remotePath
	) throws IOException {
		ftpClient.setControlEncoding("GBK");
		// 打开FTP服务
		try {
			this.openConnect();
			if(connectResult != null){
				connectResult.getConnectSuccess();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			if(connectResult != null){
				connectResult.getConnectFaild();
			}

			return;
		}

		// 设置模式
		ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
		// FTP下创建文件夹
		ftpClient.makeDirectory(remotePath);
		// 改变FTP目录
		ftpClient.changeWorkingDirectory(remotePath);
		// 上传单个文件

	}

	/**
	 * 上传完成之后关闭连接
	 *
	 * @throws IOException
	 */
	private void uploadAfterOperate()
			throws IOException {
		this.closeConnect();
		if(connectResult != null){
			connectResult.getDisConnectResult(true);
		}
	}

	// -------------------------------------------------------文件下载方法------------------------------------------------

	/**
	 * 下载单个文件，可实现断点下载.
	 *
	 * @param serverPath
	 *            Ftp目录及文件路径
	 * @param localPath
	 *            本地目录
	 * @param fileName
	 *            下载之后的文件名称
	 *            监听器
	 * @throws IOException
	 */


	public void downloadSingleFile(String serverPath, String localPath, String fileName) {

		// 打开FTP服务
		try {
			this.openConnect();
			if(connectResult != null){
				connectResult.getConnectSuccess();
			}
			// 先判断服务器文件是否存在
			FTPFile[] files = ftpClient.listFiles(serverPath);
			if (files.length == 0) {
				if(connectResult != null){
					connectResult.getFileExist(false);
				}
				return;
			}else {
				if(connectResult != null){
					connectResult.getFileExist(true);
				}
			}
			//创建本地文件夹
			File mkFile = new File(localPath);
			if (!mkFile.exists()) {
				mkFile.mkdirs();
			}

			localPath = localPath + fileName;
			// 接着判断下载的文件是否能断点下载
			long serverSize = files[0].getSize(); // 获取远程文件的长度
			File localFile = new File(localPath);
			long localSize = 0;
			if (localFile.exists()) {
				localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度
				if (localSize >= serverSize) {
					File file = new File(localPath);
					file.delete();
				}
			}

			// 开始准备下载文件
			OutputStream out = new FileOutputStream(localFile, true);
			ftpClient.setRestartOffset(localSize);
			InputStream input = ftpClient.retrieveFileStream(serverPath);
			byte[] b = new byte[1024];
			int length = 0;
			while ((length = input.read(b)) != -1) {
				out.write(b, 0, length);
			}
			out.flush();
			out.close();
			input.close();

			// 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
			if (ftpClient.completePendingCommand()) {
				if(connectResult != null){
					connectResult.getDownloadResult(true);
				}
			} else {
				if(connectResult != null){
					connectResult.getDownloadResult(false);
				}
			}
		} catch (IOException e1) {
			Log.e("ceshi2",e1.getMessage().toString());
			if(connectResult !=null){
				connectResult.getConnectFaild();
			}
			return;
		}
	}

	public void initDown(String serverPath, String localPath){
		File serverFile = new File(serverPath);
		if(serverFile.isDirectory()){
			File [] files = serverFile.listFiles();
			for(int i = 0;i<files.length;i++){
				initDown(serverPath+files[i].getName(),localPath);
			}
		}else {
			try {
				downloadSingleFile(serverPath,localPath,new File(serverPath).getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 下载完成之后关闭连接
		try {
			this.closeConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(connectResult != null){
			connectResult.getDisConnectResult(true);
		}
	}

	// -------------------------------------------------------文件删除方法------------------------------------------------

	/**
	 * 删除Ftp下的文件.
	 *
	 * @param serverPath
	 *            Ftp目录及文件路径
	 *            监听器
	 * @throws IOException
	 */
	public void deleteSingleFile(String serverPath)
			throws Exception {

		// 打开FTP服务
		try {
			this.openConnect();
			if(connectResult != null){
				connectResult.getConnectSuccess();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			if(connectResult != null){
				connectResult.getConnectFaild();
			}
			return;
		}

		// 先判断服务器文件是否存在
		FTPFile[] files = ftpClient.listFiles(serverPath);
		if (files.length == 0) {
			if(connectResult != null){
				connectResult.getFileExist(false);
			}
			return;
		}

		//进行删除操作
		boolean flag = true;
		flag = ftpClient.deleteFile(serverPath);
//		if (flag) {
//			listener.onDeleteProgress(FTP_DELETEFILE_SUCCESS);
//		} else {
//			listener.onDeleteProgress(FTP_DELETEFILE_FAIL);
//		}
//
//		// 删除完成之后关闭连接
//		this.closeConnect();
//		listener.onDeleteProgress(FTP_DISCONNECT_SUCCESS);

		return;
	}

	// -------------------------------------------------------打开关闭连接------------------------------------------------

	/**
	 * 打开FTP服务.
	 *
	 * @throws IOException
	 */
	public void openConnect() throws IOException {
		List<SystemSet>systemSets= LitePal.findAll(SystemSet.class);
		temp=OneItem.getOneItem().getSelectFTP();
		if(systemSets.size()>0){
			hostName=systemSets.get(0).getHostName();
			serverPort=systemSets.get(0).getServerPort();
			userName=systemSets.get(0).getUserName();
			password=systemSets.get(0).getPassword();
		}
		// 中文转码
		ftpClient.setControlEncoding("GBK");
		int reply; // 服务器响应值
		// 连接至服务器
		ftpClient.connect(hostName, serverPort);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}
		// 登录到服务器
		try {
			ftpClient.login(userName, password);
		}catch (Exception e){
			Log.e("ftp",e.getMessage().toString());
		}

		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else {
			// 获取登录信息
			FTPClientConfig config = new FTPClientConfig(ftpClient
					.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);
			// 使用被动模式设为默认
			ftpClient.enterLocalPassiveMode();
			// 二进制文件支持
			ftpClient
					.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		}
	}

	/**
	 * 关闭FTP服务.
	 *
	 * @throws IOException
	 */
	public void closeConnect() throws IOException {
		if (ftpClient != null) {
			// 退出FTP
			ftpClient.logout();
			// 断开连接
			ftpClient.disconnect();
		}
	}

	/**
	 * 连接成功监听
	 */
	public interface ConnectResult{
		void getConnectSuccess();//登录成功
		void getConnectFaild();//登录失败
		void getFileExist(boolean isFileExist);//文件是否存在
		void getDownloadResult(boolean isDownSuccess);//是否下载成功
		void getDisConnectResult(boolean isDisConnect);//是否成功断开连接
		void getUploadResult(boolean isUpload);//是否上传成功
	}
	static ConnectResult connectResult;
	public static void setConnectResultListener(ConnectResult connectResultListener){
		connectResult = connectResultListener;
	}

}
