public class FtpTest {
    public static void main(String[] args) {
        try {
            FtpClient client = new FtpClient();
            client.connect("cs4065", "test");
            client.getFile("ftp_test.txt");
            // client.getFile("ftp_test2.txt");
            // client.getFile("ftp_test3.txt");
            client.disconnect();
            System.out.println("File downloaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
