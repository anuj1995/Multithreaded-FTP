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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map.Entry;


public class myftp{
	
	private Socket socket = null;
	private String OS ;
	private String slash;
	private String[] arr;
	public PrintStream ps = null;
	public HashMap<Integer, String> CommandDict = new HashMap<Integer, String>();
	public InputStreamReader is = null;
	public BufferedReader br = null;
	public static String serverAddress; 
	public static int serverportNumber;
	public static int terminateportNumber;
	
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the server adress");
		serverAddress = sc.nextLine();
		System.out.println("Enter the normal port number");
		serverportNumber = sc.nextInt();
		System.out.println("Enter the terminate port number");
		terminateportNumber = sc.nextInt();
		myftp myClient = new  myftp();
		myClient.InitializeAndRun(serverAddress, serverportNumber);
	}
	
	private  void InitializeAndRun(String serverAddress,int serverportNumber) {
		try {
			socket = new Socket(serverAddress, serverportNumber);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scanner sc = new Scanner(System.in);
		String sending_line = "";
		OS = System.getProperty("os.name").toLowerCase();
		if(OS.indexOf("win") >= 0)
		slash ="\\";
		else if((OS.indexOf("mac") >= 0 || OS.indexOf("nux") >= 0))
		slash="/";
		
		while (!sending_line.equals("quit")) {
			System.out.print("myftp > ");
			sending_line = sc.nextLine();
			arr = sending_line.split(" ", 2);
			switch (arr[0]) {
			
			case"get":
				String serverPATH = "";
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println("pwd");
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					serverPATH = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				if (sending_line.endsWith("&")) {
					
					Thread getFileThread = new Thread(new getFile(sending_line, serverAddress, serverportNumber, terminateportNumber, serverPATH));
					getFileThread.start();

				} else {
					Thread getThread = new Thread(new get(sending_line, serverAddress, serverportNumber, serverPATH));
					getThread.start();
				}
				break;
			case"put":
				serverPATH = "";
				
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println("pwd");
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					serverPATH = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (sending_line.endsWith("&")) {
					Thread putFileThread = new Thread(new putFile(sending_line, serverAddress, serverportNumber, terminateportNumber, serverPATH));
					putFileThread.start();
				} else {
					Thread putThread = new Thread(new put(sending_line, serverAddress, serverportNumber, serverPATH));
					putThread.start();
				}

				break;
			case"terminate":
				String terminateMessage= "";
				Socket terminateSocket;
				try {
					terminateSocket = new Socket(serverAddress, terminateportNumber);
					ps = new PrintStream(terminateSocket.getOutputStream());
					ps.println(sending_line);
					is = new InputStreamReader(terminateSocket.getInputStream());
					br = new BufferedReader(is);
					terminateMessage = br.readLine();
				} catch (UnknownHostException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if (terminateMessage.equals("getTerminated"))
				{
					System.out.println(terminateMessage);
					int commandID = Integer.parseInt(sending_line.substring(10));
					CommandDict.remove(commandID);
				} else if (terminateMessage.equals("putTerminated")) {
					int commandID = Integer.parseInt(sending_line.substring(10));
					CommandDict.remove(commandID);
				} else {
					System.out.println(terminateMessage);
				}
				break;
			case"delete":
				String message ="";
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println(sending_line);
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					message = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(message);
				break;
				
			case"ls":
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println("ls");

					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					String lsCommand;
					while (!(lsCommand = br.readLine()).equals(""))
						System.out.println(lsCommand);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case"mkdir":
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println(sending_line);
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					String mkdirCommand = br.readLine();
					System.out.println(mkdirCommand);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
				
			case"cd":
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println(sending_line);
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					String cdCommand = br.readLine();
					System.out.println(cdCommand);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case"pwd":
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println(sending_line);
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					String cdCommand = br.readLine();
					System.out.println(cdCommand);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			case"quit":

				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println("quit");
					is = new InputStreamReader(socket.getInputStream());
					br = new BufferedReader(is);
					String quit = br.readLine();
					System.out.println(quit);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			case "exit":
				try {
					ps = new PrintStream(socket.getOutputStream());
					ps.println("exit");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("Command Invalid!");
				break;
			}
		}
	}
	public class put implements Runnable{
		
		public ObjectInputStream ois = null;
		public ObjectOutputStream oos = null;
		public DataOutputStream fDos = null;
		public DataInputStream fDis = null;
		public InputStreamReader is = null;
		public BufferedReader br = null;
		public String req = "";
		public Socket fpSocket;
		String fpAddress;
		String serverPATH;
		int fNport;
	
		
		public put(String req, String ipAddress, int nport, String serverPATH) {
			this.req = req;
			fpAddress = ipAddress;
			fNport = nport;
			this.serverPATH = serverPATH;
		}
		@Override
		public void run() {
			try {
				fpSocket = new Socket(fpAddress, fNport);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				String fileName = req.substring(4);
				String pwd = System.getProperty("user.dir");
				File putFile = new File(pwd + File.separator + fileName);

				if (putFile.exists())

				{
					ps = new PrintStream(fpSocket.getOutputStream());
					ps.println(req);
					ps.flush();

					ps = new PrintStream(fpSocket.getOutputStream());
					ps.println(serverPATH);
					ps.flush();

					boolean putConflict = false;
					is = new InputStreamReader(fpSocket.getInputStream());
					br = new BufferedReader(is);
		
					String messagefromServer = br.readLine();

					if (messagefromServer.contains("Conflict")) {
						putConflict = true;
						System.out.println(
								"File is currently in use by other Client...");
						boolean Conflict = true;
						while (Conflict) {
							is = new InputStreamReader(fpSocket.getInputStream());
							br = new BufferedReader(is);

							String conflictMessage = br.readLine();
							if (conflictMessage.equals("NoConflict")) {

								Conflict = false;
								putConflict = false;

								is = new InputStreamReader(fpSocket.getInputStream());
								br = new BufferedReader(is);
							}

						}
					}

					if (putConflict == false) {
			

						int filesize = (int) putFile.length();

						// sending file size to client
						ps = new PrintStream(fpSocket.getOutputStream());
						ps.println(filesize);
						ps.flush();

						// buffer
						byte[] buffer = new byte[1000];

						BufferedOutputStream bos = new BufferedOutputStream(fpSocket.getOutputStream());

						BufferedInputStream is1 = new BufferedInputStream(new FileInputStream(putFile));
						while (is1.read(buffer) > 0) {
							bos.write(buffer);
							bos.flush();

						}

						is1.close();
						bos.flush();
						System.out.println("File: " + fileName + " Sent!");
					}
				} else {
					System.out.println("File does not exist !!");
				}
			}catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
	}
	public class get implements Runnable{
		
		public String req = "";
		public Socket getSocket;
		String ipAddress;
		String serverPath;
		int fNport;
		public ObjectInputStream fis = null;
		public ObjectOutputStream fos = null;
		public DataOutputStream dos = null;
		public DataInputStream dis = null;
		public InputStreamReader is = null;
		public BufferedReader br = null;

		public get(String req, String ipAddress, int nport, String serverPath) {
			this.req = req;
			this.ipAddress = ipAddress;
			this.fNport = nport;
			this.serverPath = serverPath;
		}

		@Override
		public void run() {
			try {
				getSocket = new Socket(ipAddress, fNport);

			} catch (IOException e) {

				e.printStackTrace();
			}

			try {

				ps = new PrintStream(getSocket.getOutputStream());
				ps.println(req);
				ps = new PrintStream(getSocket.getOutputStream());
				ps.println(serverPath);
				String s = req;
				String[] arr = s.split(" ");
				String filename = arr[1];
				String path = System.getProperty("user.dir");
				String actualFilePath = path + File.separator + filename;
				is = new InputStreamReader(getSocket.getInputStream());
				br = new BufferedReader(is);
				String validGet = br.readLine();
				boolean conflict = false;
				if (validGet.equals("GET")) {

					String checkConflict = br.readLine();
					if (checkConflict.equals("Conflict")) {
						conflict = true;
					}
					if (conflict == false) {
						String filesize = br.readLine();
						int getFileSize = Integer.parseInt(filesize);
						File createFile = new File(actualFilePath);
						boolean isFileAlreadyPresentwithClientNormal = createFile.exists();
						if (isFileAlreadyPresentwithClientNormal) {
							System.out.println("File Already Exist..Overwriting..!!");
							createFile.delete();
						}

						byte[] Filebuffer = new byte[1000];

						if (createFile.createNewFile()) {

							System.out.println("File: " + arr[1] + " is Downloading... " + "\n" + "myftp >");

							dos = new DataOutputStream(
									new BufferedOutputStream(new FileOutputStream(createFile)));
							dis = new DataInputStream(getSocket.getInputStream());

							int readData = 0, readSum = 0;
							int remaining = getFileSize;

							while ((readData = dis.read(Filebuffer, 0,
									Math.min(Filebuffer.length, remaining))) > 0) {
								readSum += readData;
								remaining -= readData;

								dos.write(Filebuffer, 0, readData);
							}
							dos.flush();
							dos.close();

							System.out
									.println("File :" + arr[1] + " Downloaded From Server " + "\n" + "myftp >");
						} // if
					}
				} else {
					System.out.println(validGet);
				}
				if (validGet.equals("GET") && conflict == true) {
					System.out.println("File:" + filename
							+ " is currently used by another client..!! Please try after sometime..!!!");
				}

			} catch (Exception e) {

				e.printStackTrace();
			}

		}
		
	}
	
	public class putFile implements Runnable{
		
		public ObjectInputStream fis = null;
		public ObjectOutputStream fos = null;
		public DataOutputStream dos = null;
		public DataInputStream dis = null;
		public InputStreamReader is = null;
		public BufferedReader br = null;
		public String req = "";
		public Socket fpSocket;
		String fpAddress;
		String serverPATH;
		int fNport;
		
		public putFile(String req, String ipAddress, int nport, int tport, String serverPATH) {
			this.req = req;
			fpAddress = ipAddress;
			fNport = nport;
			this.serverPATH = serverPATH;
		}
		
		@Override
		public void run() {
			try {
				fpSocket = new Socket(fpAddress, fNport);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String s = req;
			String[] arr = s.split(" ");
			String fileName = arr[1];
			String pwd = System.getProperty("user.dir");
			File putFile = new File(pwd + File.separator + fileName);
			try{
				if (putFile.exists())
	
				{
					ps = new PrintStream(fpSocket.getOutputStream());
					ps.println(req);
					ps.flush();
	
					ps = new PrintStream(fpSocket.getOutputStream());
					ps.println(serverPATH);
					ps.flush();
					boolean putConflict = false;
					is = new InputStreamReader(fpSocket.getInputStream());
					br = new BufferedReader(is);
	
					String messagefromServer = br.readLine();
	
					if (messagefromServer.equals("Conflict")) {
						putConflict = true;
						System.out.println("resource is currently in use by other Client. You are in Queue.");
						boolean Conflict = true;
						while (Conflict) {
							is = new InputStreamReader(fpSocket.getInputStream());
							br = new BufferedReader(is);
	
							String conflictMessage = br.readLine();
							if (conflictMessage.equals("NoConflict")) {
								Conflict = false;
								putConflict = false;
	
								is = new InputStreamReader(fpSocket.getInputStream());
								br = new BufferedReader(is);
							}
	
						}
					}
	
					if (putConflict == false) {
						
						is = new InputStreamReader(fpSocket.getInputStream());
						br = new BufferedReader(is);
						String putCommandID = br.readLine();
						//System.out.println("in fileInuse:" + putCommandID);
						int CommandID = Integer.parseInt(putCommandID);
						System.out.println("Terminate ID:" + putCommandID + " for put FileName:" + fileName);
						CommandDict.put(CommandID, fileName);
						int filesize = (int) putFile.length();
						ps.println(filesize);
						ps.flush();
						BufferedOutputStream outputStream = new BufferedOutputStream(fpSocket.getOutputStream());
						byte[] buffer = new byte[1000];
						boolean checkTerminate = CommandDict.containsKey(CommandID);
						BufferedInputStream is1 = new BufferedInputStream(new FileInputStream(putFile));
						while (is1.read(buffer) > 0 && checkTerminate) {
							outputStream.write(buffer);
							checkTerminate = CommandDict.containsKey(CommandID);
							outputStream.flush();
						}
						is1.close();
						outputStream.flush();
						if (CommandDict.containsKey(CommandID))
							System.out.println("File: " + fileName + " Sent to the Client");
						else
							System.out.println("Put File:" + fileName + " Terminated by Client");
						if (CommandDict.containsKey(CommandID))
							CommandDict.remove(CommandID);
					}
				} else {
					System.out.println("File does not exist ");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}		
	}
	
	public class getFile implements Runnable{
		public String req = "";
		public Socket fgSocket;
		String fpAddress;
		String serverPath;
		int fnPort;
		public ObjectInputStream fis = null;
		public ObjectOutputStream fos = null;
		public DataOutputStream dos = null;
		public DataInputStream dis = null;
		public InputStreamReader is = null;
		public BufferedReader br = null;

		public getFile(String req, String ipAddress, int nport, int tport, String serverPath) {
			this.req = req;
			this.fpAddress = ipAddress;
			this.fnPort = nport;
			this.serverPath = serverPath;
		}

		@Override
		public void run() {
			try {
				fgSocket = new Socket(fpAddress, fnPort);

			} catch (IOException e) {

				e.printStackTrace();
			}
			try {

				ps = new PrintStream(fgSocket.getOutputStream());
				ps.println(req);

				ps = new PrintStream(fgSocket.getOutputStream());
				ps.println(serverPath);

				String s = req;
				String[] arr = s.split(" ");

				String firstArgument = arr[0];
				String secondArgument = arr[1];
				String thirdArgument = arr[2];

				String filename = secondArgument;

				String path = System.getProperty("user.dir");
				String actualFilePath = path + File.separator + filename;
				is = new InputStreamReader(fgSocket.getInputStream());
				br = new BufferedReader(is);
				String validGet = br.readLine();
				boolean conflict = false;
				if (validGet.equals("ThreadedGET")) {
					String checkConflict = br.readLine();

					if (checkConflict.equals("Conflict")) {
						conflict = true;
					}
					if (conflict == false) {
						String getCommandID = br.readLine();

						int CommandID = Integer.parseInt(getCommandID);
						System.out.println("Your Terminate ID is :" + CommandID);

						CommandDict.put(CommandID, filename);
						for (Entry<Integer, String> entry : CommandDict.entrySet()) {
						}
						String filesize = br.readLine();
						int getFileSize = Integer.parseInt(filesize);
						File createFile = new File(actualFilePath);

						boolean isFileAlreadyPresentwithClient = createFile.exists();
						if (isFileAlreadyPresentwithClient) {
							System.out.println("File already exsist..Overwriting !!!");
							createFile.delete();
						}
						byte[] Filebuffer = new byte[1000];

						if (createFile.createNewFile()) {

							System.out.println("File: " + secondArgument + " is Downloading... " + "\n" + "myftp >");

							dos = new DataOutputStream(
									new BufferedOutputStream(new FileOutputStream(createFile)));
							dis = new DataInputStream(fgSocket.getInputStream());

							boolean checkTerminate = CommandDict.containsKey(CommandID);

							int readData = 0, readSum = 0;
							int remaining = getFileSize;
							while ((readData = dis.read(Filebuffer, 0,
									Math.min(Filebuffer.length, remaining))) > 0 && checkTerminate) {

								readSum += readData;
								remaining -= readData;
								dos.write(Filebuffer, 0, readData);
								checkTerminate = CommandDict.containsKey(CommandID);

								if (!checkTerminate) {
									createFile.delete();
								}

							}
							dos.close();

							if (CommandDict.containsKey(CommandID)) {

								System.out.println("File :" + filename + " Downloaded From Server " + "\n" + "myftp >");

							}

							if (!CommandDict.containsKey(CommandID)) {
								createFile.delete();
							}
							if (CommandDict.containsKey(CommandID))
								CommandDict.remove(CommandID);

						} // if
					} else if (validGet.equals("ThreadedGET") && conflict == true) {
						System.out.println("File:" + filename
								+ " is currently used by another client.");
					}
				} else {
					System.out.println(validGet);
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		
	}
}