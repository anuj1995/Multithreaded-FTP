

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;


public class myftpserver {
	public ServerSocket terminateserSocket;
	static int serverPortNumber = 0;
	static int terminatePortNumber = 0;
	String commandIntials;
	public Socket clientSocket;
	public ServerSocket serSocket;
	
	public HashMap<Integer, String> CommandDict = new HashMap<Integer, String>();
	static final public Path homeDirectory = Paths.get(System.getProperty("user.dir"));
	
	public HashMap<Integer, String> getCommandHashMap() {
		return CommandDict;
	}

	public void setCommandHashMap(HashMap<Integer, String> CommandDict) {
		this.CommandDict = CommandDict;
	}
	public int getCommandID() {
		return CommandID;
	}

	public void setCommandID(int commandID) {
		CommandID = commandID;
	}



	public int CommandID = 4500;
	public myftpserver(int serverPortNumber, int terminatePortNumber) {

		Thread allCommandsThread = null;
		Thread terminateCommandThread = null;

		try {
			// Normal Port
			serSocket = new ServerSocket(serverPortNumber);
			allCommandsThread = new Thread(new AllCommandsHandler(serverPortNumber, serSocket));
			allCommandsThread.start();

			// terminateServer
			terminateserSocket = new ServerSocket(terminatePortNumber);
			terminateCommandThread = new Thread(new SuspendConnection(terminateserSocket));
			terminateCommandThread.start();

		} catch (Exception e) {
			System.out.printf("Error occured\n");
		}
	}
	public myftpserver() {}
	public static void main(String[] args) throws IOException {
		System.out.println("Please enter the normal port no:");
		Scanner sc = new Scanner(System.in);
		serverPortNumber = sc.nextInt();
		System.out.println("Please enter the terminate port no:");
		terminatePortNumber = sc.nextInt();
		myftpserver myServer = new myftpserver(serverPortNumber, terminatePortNumber);
	}
	class AllCommandsHandler implements Runnable {

		private Socket clientSocket;
		private int serverPortNumber;
		private ServerSocket serSocket2;
		public AllCommandsHandler(int serverPortNumber, ServerSocket serSocket2) {
			this.serSocket2 = serSocket2;
			this.serverPortNumber = serverPortNumber;
		}
		@Override
		public void run() {
			try {
				System.out.println("\n Server is Running ");
				while (true) {
					clientSocket = serSocket2.accept();
					Thread thread = new Thread(new ServerThread(clientSocket));
					thread.start();
					System.out.println("Thread:" + thread.getName());
				}
			} catch (BindException e) {
				System.out.println("Bind failed");
			} catch (NullPointerException e) {

				System.out.println("Bind failed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	class ServerThread implements Runnable {

		public Socket clientSocket;
		public InputStreamReader serverInputStream;
		public BufferedReader serBufferReader;
		public String userInput;
		public String commandIntials;
		public ObjectOutputStream serOutputStream12;
		public PrintStream serOutputStream;
		public PrintStream serOutputStream1 = null;
		boolean loop = true;
		public DataOutputStream outputStream = null;
		public DataInputStream inputstream = null;
		public DataOutputStream dopstream = null;
		public DataInputStream dis = null;
		Path clientPath = null;

		public ServerThread(Socket serSocket) {
			this.clientSocket = serSocket;
			System.out.println("Client Connected");
			clientPath = Paths.get(homeDirectory.toString());
		}
		@Override
		public void run() {
			String userHomeDirectory = clientPath.toString();
			while (loop) {
				try {
					serverInputStream = new InputStreamReader(clientSocket.getInputStream());
					serBufferReader = new BufferedReader(serverInputStream);
					userInput = serBufferReader.readLine();

					if (userInput != null)
						commandIntials = getActualCommand(userInput);

					if (commandIntials != null)
						if (commandIntials.equals("pwd")) {

							serOutputStream = new PrintStream(clientSocket.getOutputStream());

							String pwd = clientPath.toString();
							serOutputStream.println(pwd);

						} else if (commandIntials.equals("ls")) {

							File dir = new File(clientPath.toString());
							String dirtory[] = dir.list();
							String allDirectories = "";

							for (String dirName : dirtory) {
								allDirectories = allDirectories + dirName + "\n";
							}
							serOutputStream = new PrintStream(clientSocket.getOutputStream());
							serOutputStream.println(allDirectories);
						} else if (commandIntials.equals("mkdir")) {
							String dirName = null;
							try {
								dirName = userInput.substring(userInput.indexOf(' '));
								dirName = dirName.trim();
							} catch (Exception e1) {
								String dirNameNotSpecified = "Directory Name required!";
								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println(dirNameNotSpecified);
								return;
							}
							String dirtPath = clientPath.toString();
							String directoryCreation = dirtPath + "/" + dirName;
							Path dirtNameC = Paths.get(directoryCreation);
							try {
								Files.createDirectory(dirtNameC);

								String directoryCreated = "Directory Created Successfully! ";

								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println(directoryCreated);

							} catch (FileAlreadyExistsException e) {

								String directoryAlreadyExits = "Directory Already exits!";
								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println(directoryAlreadyExits);

							} catch (Exception e) {
							}

						} else if (commandIntials.equals("cd")) {
							String cdCommandArguments = null;
							try {
								cdCommandArguments = userInput.substring(userInput.indexOf(' '));
								cdCommandArguments = cdCommandArguments.trim();

							} catch (Exception e) {
								cdCommandArguments = "cd";
							}
							if (cdCommandArguments.equals("cd")) {

								Path path2 = clientPath;
								clientPath = path2.resolve(userHomeDirectory);
								String home_dir = clientPath.toString();
								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println(home_dir);
							} else {

								if (cdCommandArguments.equals("..")) {
									String path = clientPath.toString();
									String previousDirectory = null;
									try {
										previousDirectory = path.substring(0, path.lastIndexOf("/"));
										previousDirectory = previousDirectory.trim();
									} catch (Exception e) {

										previousDirectory = "/";
									}
									if (previousDirectory.equals("/")) {
									}
									Path path1 = clientPath;
									clientPath = path1.resolve(previousDirectory);

									System.out.println("CLIENTPATH:" + clientPath.toString());
									String parentDir = previousDirectory;
									if (parentDir.equals("")) {
										parentDir = "/";
									}

									serOutputStream = new PrintStream(clientSocket.getOutputStream());
									serOutputStream.println(parentDir);
									serOutputStream.flush();

								} else {

									Path path3 = clientPath;

									if (Files.notExists(path3.resolve(cdCommandArguments))) {
										String noDirectoryExitsMsg = "No Directory Exits!";
										serOutputStream = new PrintStream(clientSocket.getOutputStream());
										serOutputStream.println(noDirectoryExitsMsg);
										serOutputStream.flush();
									}

									else if (Files.isDirectory(path3.resolve(cdCommandArguments))) {

										path3 = path3.resolve(cdCommandArguments);
										clientPath = path3;
										String change_dir_path = clientPath.toString();
										serOutputStream = new PrintStream(clientSocket.getOutputStream());
										serOutputStream.println(change_dir_path);
										serOutputStream.flush();
									}
									else {
										String msg = "Not a Directory";
										serOutputStream = new PrintStream(clientSocket.getOutputStream());
										serOutputStream.println(msg);
									}
								}
							}
						} else if (commandIntials.equals("delete")) {

							if (userInput.length() > 6) {
								String DelFileName = userInput.substring(7);
								String pwd = clientPath.toString();
								File FileName = new File(pwd + File.separator + DelFileName);
								boolean checkDeletion = checkifFileInUse(FileName);
								boolean doesFileExits = FileName.exists();
								if (doesFileExits) {
									if (checkDeletion) {
										FileName.delete();
										System.out.println("File :" + DelFileName + " is deleted..");
										serOutputStream = new PrintStream(clientSocket.getOutputStream());
										serOutputStream.println("File :" + DelFileName + " is deleted");
									} else {
										serOutputStream = new PrintStream(clientSocket.getOutputStream());
										serOutputStream.println("File :" + DelFileName + " is in USE!");
									}
								} else {
									serOutputStream = new PrintStream(clientSocket.getOutputStream());
									serOutputStream.println("File :" + DelFileName + " does not exist!!");
									serOutputStream.flush();
								}
							} else {
								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println("Please provide the file name");
								serOutputStream.flush();
							}

						} else if (commandIntials.equals("put")) {
							String serverPATHreceivedfromCLIENT = serBufferReader.readLine();
							System.out.println("serverPATHreceivedfromCLIENT:" + serverPATHreceivedfromCLIENT);
							System.out.println("clientRequest: " + userInput);
							System.out.println("commandIntials: " + commandIntials);

							String[] arr = userInput.split(" ");

							String fileName = arr[1];
							String pwd = serverPATHreceivedfromCLIENT;

							File FileName = new File(pwd + File.separator + fileName);
							boolean checkPutConflict = checkifFileInUse(FileName);

							boolean queueCheck = true;
							if (checkPutConflict == false) {
								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println("Conflict");
								serOutputStream.flush();
								while (queueCheck) {
									checkPutConflict = checkifFileInUse(FileName);
									if (checkPutConflict == true) {
										queueCheck = false;
										serOutputStream = new PrintStream(clientSocket.getOutputStream());
										serOutputStream.println("NoConflict");
										serOutputStream.flush();
									}
								}
							}
							if (checkPutConflict == true) { 
								System.out.println("conflict true");
								boolean isPUTsameFileExits = checkifSameFileisPUT(userInput);
								System.out.println("In isPUTsameFileExits" + isPUTsameFileExits);
								if (isPUTsameFileExits) {
									serOutputStream = new PrintStream(clientSocket.getOutputStream());
									serOutputStream.println(
											"File Already Exits on Sever.!!! Sending File -->Overwriting on Server..!!");
									serOutputStream.flush();

									if (userInput.endsWith("&")) {
										PUTFileThread(userInput, serverPATHreceivedfromCLIENT);
									} else {
										PUTFile(userInput, serverPATHreceivedfromCLIENT);
									}
								}
								if (isPUTsameFileExits == false) {
									serOutputStream = new PrintStream(clientSocket.getOutputStream());
									serOutputStream.println("File Exist..Sending..");
									serOutputStream.flush();
									if (userInput.endsWith("&")) {
										PUTFileThread(userInput, serverPATHreceivedfromCLIENT);
									} else {
										PUTFile(userInput, serverPATHreceivedfromCLIENT);
									}
								}
							}
						} else if (commandIntials.equals("get")) {
							String serverPATHreceivedfmCLIENTforGET = serBufferReader.readLine();
							System.out.println("serverPATHreceivedfromCLIENT:" + serverPATHreceivedfmCLIENTforGET);
							System.out.println("clientRequest: " + userInput);
							System.out.println("commandIntials: " + commandIntials);
							try {
								if (userInput.endsWith("&")) {
									getFileThread(userInput, serverPATHreceivedfmCLIENTforGET);
								} else {
									getFile(userInput, serverPATHreceivedfmCLIENTforGET);
								}
							} catch (Exception e) {
								System.out.println("");
							}
							commandIntials = null;
						} else if (commandIntials.equals("quit") || commandIntials.equals("exit")) {
							try {
								loop = false;
								String quitFTPClient = "FTP Client Closed!";
								serOutputStream = new PrintStream(clientSocket.getOutputStream());
								serOutputStream.println(quitFTPClient);
								serOutputStream.flush();
								clientSocket.close();
								// Normal Port
								serSocket = new ServerSocket(serverPortNumber);
								Thread allCommandsThread = new Thread(new AllCommandsHandler(serverPortNumber, serSocket));
								allCommandsThread.start();

								// terminateServer
								terminateserSocket = new ServerSocket(terminatePortNumber);
								Thread terminateCommandThread = new Thread(new SuspendConnection(terminateserSocket));
								terminateCommandThread.start();
							} catch (Exception e) {
								System.out.println("FTP Client Exited");
							}
						} else if (userInput == null) {
							break;
						}
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
		private boolean checkifSameFileisPUT(String userInput2) throws IOException {

			String s = userInput2;
			String[] arr = s.split(" ");
			System.out.println("Size of array:" + arr.length);
			String actualFileName = arr[1];
			System.out.println("Actual File Name: " + actualFileName);
			String path = System.getProperty("user.dir");
			String actualFilePath = path + File.separator + actualFileName;
			System.out.println("Server File Path: " + actualFilePath);

			File checkFileOnServer = new File(actualFilePath);

			if (checkFileOnServer.exists()) {
				System.out.println("File Already Present on Server");
				

				if (checkFileOnServer.delete()) {
					System.out
							.println("File with same name was deleted on server \n Now it is going to be overwrite!! ");
				}
				return true;
			} else {
				return false;
			}
		}
		private boolean checkifFileInUse(File fileName) {
			boolean isFileInUse = true;
			String getfileName = fileName.getName();
			System.out.println("FileName:Checking if it can be Deleted:" + fileName.getName());
			System.out.println("FilePath: " + fileName.getPath());
			for (Entry<Integer, String> entry : CommandDict.entrySet()) {
				System.out.println("\nFileName from HashMAP: " + entry.getValue());
				String fileNameFromHASHMAP = entry.getValue().substring(3);
				System.out.println("\nFileName from HashMAP:" + fileNameFromHASHMAP);
				if (getfileName.equals(fileNameFromHASHMAP)) {
					isFileInUse = false;
				}
			}
			return isFileInUse;
		}
		private void PUTFile(String userInput2, String serverPATH) throws IOException {
			// TODO Auto-generated method stub

			String firstArgument;
			String secondArgument;
			String s = userInput2;
			String[] arr = s.split(" ");
			System.out.println("Size of array in PUTFile:" + arr.length);
			firstArgument = arr[0];
			secondArgument = arr[1];
			System.out.println(firstArgument);
			System.out.println(secondArgument);

			DataInputStream inputstream = new DataInputStream(clientSocket.getInputStream());
			String filename = secondArgument;

			String path = serverPATH;
			String actualFilePath = path + File.separator + filename;
			int currentCommandID = CommandID;
			CommandID++;
			CommandDict.put(currentCommandID, "put" + filename);
			System.out.println("The Current Values in hashmap in NORMAL");
			for (Entry<Integer, String> entry : CommandDict.entrySet()) {
				System.out.println(entry.getKey() + ", " + entry.getValue());
			}
			File createFile = new File(actualFilePath);
			String filesize = serBufferReader.readLine();
			int getFileSize = Integer.parseInt(filesize);
			byte[] buffer = new byte[1000];
			if (createFile.createNewFile()) {
				System.out.println("File :" + filename + " Uploading From Client");
				DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(createFile)));
				int readData = 0;
				int remaining = getFileSize;
				while ((readData = inputstream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
					remaining -= readData;
					outputStream.write(buffer, 0, readData);
					}
				outputStream.flush();
				outputStream.close();
				if (CommandDict.containsKey(currentCommandID)) {
					System.out.println("File :" + filename + " Uploaded");
					CommandDict.remove(currentCommandID);
				}
			} // if
			else {
				System.out.println("Invalid  Upload");
			}
			userInput = null;
		}

		private void PUTFileThread(String userInput, String serverPATH) throws IOException {
			String firstArgument;
			String secondArgument;
			String thirdArgument;
			String s = userInput;
			String[] arr = s.split(" ");
			System.out.println("Size of array :" + arr.length);

			firstArgument = arr[0];
			secondArgument = arr[1];
			thirdArgument = arr[2];

			System.out.println(firstArgument);
			System.out.println(secondArgument);
			System.out.println(thirdArgument);

			DataInputStream inputstream = new DataInputStream(clientSocket.getInputStream());
			String filename = secondArgument;

			String path = serverPATH;
			String actualFilePath = path + File.separator + filename;

			serOutputStream = new PrintStream(clientSocket.getOutputStream());
			int currentCommandID = CommandID;
			CommandID++;
			CommandDict.put(currentCommandID, "put" + filename);
			serOutputStream.println(currentCommandID);
			serOutputStream.flush();

			System.out.println("The Current Values in hashmap in put thread");
			for (Entry<Integer, String> entry : CommandDict.entrySet()) {
				System.out.println(entry.getKey() + ", " + entry.getValue());
			}
			File createFile = new File(actualFilePath);
			String filesize = serBufferReader.readLine();
			int getFileSize = Integer.parseInt(filesize);

			System.out.println("Filesize before if" + getFileSize);
			// buffer for storage
			byte[] buffer = new byte[1000];
			if (createFile.createNewFile()) {
				System.out.println("Filesize i if" + getFileSize);
				DataOutputStream outputStream = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(createFile)));

				boolean checkTerminate = CommandDict.containsKey(currentCommandID);

				int readData = 0, readSum = 0;
				int remaining = getFileSize;
				while ((readData = inputstream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0
						&& checkTerminate) {
					readSum += readData;
					remaining -= readData;
					outputStream.write(buffer, 0, readData);
					checkTerminate = CommandDict.containsKey(currentCommandID);
				} // while
				System.out.println("Put after While");
				if (!checkTerminate) {
					outputStream.flush();
					outputStream.close();
					System.out.println("Terminated the put process for File:" + filename);
					createFile.delete();
				}
				outputStream.close();
				System.out.println("Put Display Hash Map");
				for (Entry<Integer, String> entry : CommandDict.entrySet()) {
					System.out.println(entry.getKey() + ", " + entry.getValue());
				}
				if (CommandDict.containsKey(currentCommandID)) {
					System.out.println("File :" + filename + " Uploaded");
					CommandDict.remove(currentCommandID);
				}
			} // if
			else {
				System.out.println("Invalid File Upload");
			}
		}
		private void getFile(String userInput2, String serverPATHreceivedfmCLIENTforGETNormal) throws IOException {

			String firstArgument;
			String secondArgument;

			String s = userInput2;

			String[] arr = s.split(" ");
			System.out.println("Size of array:" + arr.length);

			firstArgument = arr[0];
			secondArgument = arr[1];

			System.out.println(firstArgument);
			System.out.println(secondArgument);

			dopstream = new DataOutputStream(clientSocket.getOutputStream());
			String getFileName = secondArgument;

			String pwd = serverPATHreceivedfmCLIENTforGETNormal;
			System.out.println(pwd);

			File getFile = new File(pwd + File.separator + getFileName);

			if (getFile.exists()) {

				System.out.println("inside server File Exits");
				serOutputStream1 = new PrintStream(clientSocket.getOutputStream());
				serOutputStream1.println("GET");
				serOutputStream1.flush();

				boolean checkPutConflict = checkifFileInUse(getFile);

				if (checkPutConflict == false) {

					System.out.println("COnflict getNormal");
					serOutputStream = new PrintStream(clientSocket.getOutputStream());
					serOutputStream.println("Conflict");
					serOutputStream.flush();
				} else {
					serOutputStream = new PrintStream(clientSocket.getOutputStream());
					serOutputStream.println("NoConflict");
					serOutputStream.flush();

				}
				if (checkPutConflict == true) {
					// Get CommandID
					int currentCommandID = CommandID;
					CommandID++;

					// adding into hashMap
					CommandDict.put(currentCommandID, "get Normal" + getFileName);
					System.out.println("Value of Hashmap after start of Normal Get Command");

					for (Entry<Integer, String> entry : CommandDict.entrySet()) {
						System.out.println(entry.getKey() + ", " + entry.getValue());
					}
					int filesize = (int) getFile.length();
					serOutputStream1 = new PrintStream(clientSocket.getOutputStream());
					serOutputStream1.println(filesize);
					byte[] buffer = new byte[1000]; // Buffer
					dis = new DataInputStream(new BufferedInputStream(new FileInputStream(getFile)));
					while (dis.read(buffer) > 0) {
						dopstream.write(buffer);
					}
					dis.close();
					if (CommandDict.containsKey(currentCommandID)) {
						CommandDict.remove(currentCommandID);
						System.out.println("File: " + getFileName + " Sent to the Client..!!");
					}
				}
			} else {
				serOutputStream1 = new PrintStream(clientSocket.getOutputStream());
				serOutputStream1.println("File " + getFileName + " not found");
				serOutputStream1.flush();
				System.out.println("Client File " + getFileName + " not found");
			}
		}
		private void getFileThread(String clientRequest, String serverPATHreceivedfmCLIENTforGET) throws IOException {

			String firstArgument;
			String secondArgument;
			String thirdArgument;
			String s = clientRequest;
			String[] arr = s.split(" ");
			System.out.println("Size of array:" + arr.length);

			firstArgument = arr[0];
			secondArgument = arr[1];
			thirdArgument = arr[2];

			String getFileName = secondArgument;
			System.out.println(firstArgument);
			System.out.println(secondArgument);
			System.out.println(thirdArgument);

			dopstream = new DataOutputStream(clientSocket.getOutputStream());
			String pwd = serverPATHreceivedfmCLIENTforGET;
			System.out.println("serverPATHreceivedfmCLIENTforGET" + serverPATHreceivedfmCLIENTforGET);

			File getFile = new File(pwd + File.separator + getFileName);

			if (getFile.exists()) {

				System.out.println("inside server File Exits");

				serOutputStream1 = new PrintStream(clientSocket.getOutputStream());
				serOutputStream1.println("ThreadedGET");
				serOutputStream1.flush();

				// check for put-get conflict
				boolean checkPutConflict = checkifFileInUse(getFile);

				if (checkPutConflict == false) {

					System.out.println("COnflict getThread");
					serOutputStream = new PrintStream(clientSocket.getOutputStream());
					serOutputStream.println("Conflict");
					serOutputStream.flush();
				} else {
					serOutputStream = new PrintStream(clientSocket.getOutputStream());
					serOutputStream.println("NoConflict");
					serOutputStream.flush();

				}

				if (checkPutConflict == true) {
					int currentCommandID = CommandID;
					CommandID++;

					// adding into hashMap
					CommandDict.put(currentCommandID, "get" + getFileName);
					System.out.println("if value of curret ID" + CommandDict.containsKey(currentCommandID));

					for (Entry<Integer, String> entry : CommandDict.entrySet()) {
						System.out.println(entry.getKey() + ", " + entry.getValue());
					}
					// Sending Client Get CommandID
					serOutputStream1.println(currentCommandID);
					System.out.println("curr" + currentCommandID + "new" + CommandID);
					serOutputStream1.flush();

					int filesize = (int) getFile.length();
					// sending file size to client
					serOutputStream1 = new PrintStream(clientSocket.getOutputStream());
					serOutputStream1.println(filesize);
					// serOutputStream.flush();

					byte[] buffer = new byte[1000]; // Buffer
					dis = new DataInputStream(new BufferedInputStream(new FileInputStream(getFile)));

					// check if get is Terminated from HashMap entry exist
					boolean checkTerminate = CommandDict.containsKey(currentCommandID);
					// reading and writing into file
					while (dis.read(buffer) > 0 && checkTerminate) {
						dopstream.write(buffer);
						checkTerminate = CommandDict.containsKey(currentCommandID);
					}
					dis.close();
					if (CommandDict.containsKey(currentCommandID))
						System.out.println("File: " + getFileName + " Sent to the Client..!!");
					else
						System.out.println("Get File:" + getFileName + " Terminated by Client..!!");
					if (CommandDict.containsKey(currentCommandID))
						CommandDict.remove(currentCommandID);
				}
			} else {
				serOutputStream1 = new PrintStream(clientSocket.getOutputStream());
				serOutputStream1.println("File " + getFileName + " not found");
				serOutputStream1.flush();
				System.out.println("Client File " + getFileName + " not found");
			}
		}
		private String getActualCommand(String userInput) {
			String commandIntials;
			if (userInput.contains(" ")) {
				commandIntials = userInput.substring(0, userInput.indexOf(' '));
				return commandIntials;
			} else {
				commandIntials = userInput;
				return commandIntials;
			}
		}
	}
	class getCommandThread implements Runnable {
		private Socket socket;
		private DataOutputStream os;
		private int fileSize;
		private File getFile;
		private String getFileName;

		public getCommandThread(Socket clientSocket, String getFileName, File getFile, int fileSize) {
			System.out.println("CREATED COMMAND THREAD");
			try {
				this.socket = clientSocket;
				this.fileSize = fileSize;
				this.getFile = getFile;
				this.getFileName = getFileName;
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}

		public void run() {
			byte[] buffer = new byte[fileSize]; 
			try {
				DataInputStream inputstream = new DataInputStream(
						new BufferedInputStream(new FileInputStream(getFile)));
				os = new DataOutputStream(socket.getOutputStream());
				while (inputstream.read(buffer) >= 0) {
							os.write(buffer);
				}
				inputstream.close();
				os.flush();
			} catch (Exception e) {
				System.out.println("Error");
			}
			System.out.println("File: " + getFileName + " Sent to the Client");

		}
	}

	class SuspendConnection implements Runnable {

		private Socket terminateClientSocket;
		private ServerSocket TerminateSocket;

		public SuspendConnection(ServerSocket TerminateSocket) {
			System.out.println("Terminate Constructor Initialized");
			this.TerminateSocket = TerminateSocket;
		}
		@Override
		public void run() {
			try {
				while (true) {
					terminateClientSocket = TerminateSocket.accept();
					Thread thread = new Thread(new TerminateServerThread(terminateClientSocket));
					thread.start();
					System.out.println("Terminate Thread:" + thread.getName());
				}
			} catch (BindException e) {
				System.out.println("Bind failed - Try different port");
			} catch (NullPointerException e) {

				System.out.println("Bind failed - Try different port");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	class TerminateServerThread implements Runnable {
		private InputStreamReader serverInputStream = null;
		public PrintStream serOutputStream;
		private BufferedReader serBufferReader = null;
		private Socket terminateSocketClient = null;
		public String terminateUserInput;

		public TerminateServerThread(Socket terminateSocket) {
			this.terminateSocketClient = terminateSocket;
		}
		@Override
		public void run() {
			while (true) {
				try {
					serverInputStream = new InputStreamReader(terminateSocketClient.getInputStream());
					serBufferReader = new BufferedReader(serverInputStream);
					serOutputStream = new PrintStream(terminateSocketClient.getOutputStream());
					terminateUserInput = serBufferReader.readLine();
					Integer commandID = Integer.parseInt(terminateUserInput.substring(10));
					System.out.println("Terminate userInput Terminate ID " + commandID);
					for (Entry<Integer, String> entry : CommandDict.entrySet()) {
						System.out.println(entry.getKey() + ", " + entry.getValue());
					}
					if (CommandDict.containsKey(commandID)) {
						String HashValue = CommandDict.get(commandID);
						if (HashValue.startsWith("get")) {
							CommandDict.remove(commandID);
							serOutputStream.println("getTerminated");
							serOutputStream.flush();
						} else {
							CommandDict.remove(commandID);
							serOutputStream.println("putTerminated");
							serOutputStream.flush();
						}
					} else {
						serOutputStream.println("CommandID " + commandID + " is Invalid");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
