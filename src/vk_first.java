import java.net.Socket;

public class vk_first {
	
	public static void main(String[] args) {
		//Create socket, send request and receive reply
		try {
			Socket sock = new Socket("api.vk.com", 80);
			
			String req = "GET http://api.vk.com/method/users.get.xml?user_ids=1&fields=bdate HTTP/1.0 \n\n";
			
			sock.getOutputStream().write(req.getBytes());
			
			byte buf[] = new byte[64*1024];
			int r = sock.getInputStream().read(buf);
			String data = new String(buf, 0, r);
			
			System.out.println(data);
			
			sock.close();
		}
		catch(Exception e) {
			System.out.println("Error: "+e);
		}
	}
}
