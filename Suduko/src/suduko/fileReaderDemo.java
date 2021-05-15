package suduko;

import java.io.BufferedReader;
import java.io.FileReader;

public class fileReaderDemo {
	public static void main(String[] args) throws Exception {
		
		
		FileReader fr = new FileReader("C:/Users/DELL/Desktop/DoAn/Suduko/input1.txt");
		BufferedReader br = new BufferedReader(fr);
		
		String s;
		while((s = br.readLine()) !=null) {
			System.out.println(s);
		}
		fr.close();

		
	}

}
