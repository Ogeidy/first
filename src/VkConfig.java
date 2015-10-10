import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implement read, write and store configuration information.
 * @author Yakov Mamontov
 *
 */
public class VkConfig {

	public String APP_ID;
	public String ACCESS_TOKEN;
	public String UNIVERSITY_ID;
	private File configFile;
	private BufferedReader cfgRd;
	private FileWriter cfgWrt;
	
	/**
	 * Creates a new <code>VkConfig</code> instance 
	 * @param fileName
	 */
	public VkConfig(String fileName) {
		
		configFile = new File(fileName);
		
	}
	
	/**
	 *  Reads the configure file and fills all necessary fields.
	 * @return
	 */
	public int readConfig() {

		String s;
		int it = 0;
		
		try {
			cfgRd = new BufferedReader(new FileReader(configFile.getAbsoluteFile()));
			
			while ((s = cfgRd.readLine()) != null) {
				System.out.println(s.substring(s.indexOf("=")+2));
				if (it == 0) {
					APP_ID = s.substring(s.indexOf("=")+2);
					it++;
				} else if(it == 1) {
					ACCESS_TOKEN = s.substring(s.indexOf("=")+2);
					it++;
				} else if ( it == 2) {
					UNIVERSITY_ID = s.substring(s.indexOf("=")+2);
					it++;
				}
			}
			cfgRd.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: Can't write to config file");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error: Can't write to config file");
			e.printStackTrace();
			System.exit(1);
		}
		
		return 0;
	}
	
	/**
	 * Sets <code>ACCESS_TOKEN</code> and writes it to configure file.
	 * @param accessToken
	 * @return
	 */
	public int setAccessToken(String accessToken) {
		
		this.ACCESS_TOKEN = accessToken;
		
		try {
			cfgWrt = new FileWriter(configFile);
			
			cfgWrt.append("APP_ID = "+APP_ID+"\n");
			cfgWrt.append("ACCESS_TOKEN = "+ACCESS_TOKEN+"\n");
			cfgWrt.append("UNIVERSITY_ID = "+UNIVERSITY_ID+"\n");
			
			cfgWrt.flush();
			cfgWrt.close();
		} catch (IOException e) {
			System.out.println("Error: Can't write to config file.\n Confilure file was not changed.");
		}
	
		return 0;
	}
	
}
