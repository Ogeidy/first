import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class VkConfig {

	public String APP_ID;
	
	public String ACCESS_TOKEN;
	
	public String UNIVERSITY_ID;
	
	private File configFile;
	
	private BufferedReader cfgRd;
	
	public VkConfig(String fileName) throws IOException {
		
		configFile = new File(fileName);
		
		cfgRd = new BufferedReader(new FileReader(configFile.getAbsoluteFile()));
		
		// Read config file
		String s;
		int it = 0;
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
		
	}
	
}
