Distributed Computing Systems
(CSCI 6780)

Programming Project 2: Multithreaded FTP Client and Server 

This is the second project of the distributed systems course and it aims at introducing the basics of the client-server model of distributed systems. First project is extended to make both the client and server multithreaded. The client and server will support the same set of commands as indicated in project 1 (get, put, delete, ls, cd, mkdir, pwd, quit). In addition, they will support one more command called “terminate”, which is used for terminating a long-running command (e.g., get and put on large files) from the same client. 

A. Project Members:
* Anuj Panchmia
* Vishakha Atole
 
B. Compilation or execution instruction:
* The following are the two executable files:
* myftp.java
* myftpserver.java
* In order to run the FTP Client Server Application following are the steps:
1. Compile both the executable files in terminal using the following commands:
      For client file:  javac myftp.java
      For server file: javac myftpserver.java
2. Firstly, run the myftpserver.java using 
      java myftpserver
      Please enter the normal port no:
      Please enter the terminate port no:
3. Secondly, run the myftp.java using
      java myftp
      Please enter the IP address:
      Please enter the normal port no:
      Please enter the terminate port no:
4. This will establish a connection between the client and the server. Once this connection is established, we can perform the following set of commands:
      (get, put, delete, ls, cd, mkdir, pwd, quit). 
5. Following is the explanation for all these commands:
* get (get <remote_filename>) -- Copy file with the name <remote_filename> from remote directory to local directory. 
* put (put <local_filename>) -- Copy file with the name <local_filename> from local directory to remote directory. 
* delete (delete <remote_filename>) – Delete the file with the name <remote_filename> from the remote directory. 
* ls (ls) -- List the files and subdirectories in the remote directory. 
* cd (cd <remote_direcotry_name> or cd ..) – Change to the <remote_direcotry_name > on the remote machine or change to the parent directory of the current directory 
* mkdir (mkdir <remote_directory_name>) – Create directory named <remote_direcotry_name> as the sub-directory of the current working directory on the remote machine. 
* pwd (pwd) – Print the current working directory on the remote machine. 
* quit (quit) – End the FTP session. 
6. If you add a & sign in front of get or put command, then the command id is generated which is used to terminate the command.
7. Use terminate <command-id> for the terminate a long running command.

C. This project was done in its entirety by Anuj Panchmia and Vishakha Atole. We hereby state that we have not received unauthorized help of any form. 






