/**
* Assignment 1
* Nate Heath
**/


import java.io.* ;
import java.net.* ;
import java.util.* ;
public final class WebServerBackup {
    public static void main(String argv[]) throws Exception
    {
        // Set the port number.
        int port = 6789;


        // Establish the listen socket.
        ServerSocket listenSocket = new ServerSocket(port);


        // Process HTTP service requests in an infinite loop.
        while (true) {
            // Listen for a TCP connection request.
            Socket connectionSocket = listenSocket.accept();


            // Construct an object to process the HTTP request message.
            HttpRequest request = new HttpRequest(connectionSocket);
       
            // Create a new thread to process the request.
            Thread thread = new Thread(request);
       
            // Start the thread.
            thread.start();
        }
    }
}


final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
   
    // Constructor
    public HttpRequest(Socket socket) throws Exception
    {
        this.socket = socket;
    }


    // Implement the run() method of the Runnable interface.
    public void run()
    {
        try {
            processRequest();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }


    private void processRequest() throws Exception
    {
        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());


        // Set up input stream filters.
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);


        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();


        // Display the request line.
        System.out.println("Request: ");
        System.out.println(requestLine);


        // Get and display the header lines.
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }


        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();


        // Prepend a "." so that file request is within the current directory.
        fileName = "." + fileName;


        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e) {
            fileExists = false;
        }


        // Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;


        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
        }
        else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
        }


        // Send the status line.
        os.writeBytes(statusLine);


        // Send the content type line.
        os.writeBytes(contentTypeLine);


        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);


        System.out.println("Response: ");
        System.out.println(statusLine);
        System.out.println(contentTypeLine);
        System.out.println(CRLF);
       


        // Send the entity body.
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        }
        else {
            os.writeBytes(entityBody);
            System.out.println(entityBody);
        }
       
        is.close();
        os.close();
        br.close();
        socket.close();
    }


    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
       
        // Copy requested file into the socket's output stream.
        while((bytes = fis.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes);
        }
    }


    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}

