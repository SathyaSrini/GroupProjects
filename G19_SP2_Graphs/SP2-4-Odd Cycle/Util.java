import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {
	
	/**
	 * Write the output to the file provided in the file name
	 * @param fileName - Name of the file where the output should be written
	 * @param output - Content to be written in to the file
	 */
	public static void printOutput(final String fileName, final String output)
	{
		try
		{
			BufferedOutputStream outputStream = new BufferedOutputStream(
													new FileOutputStream(fileName));
			outputStream.write(output.getBytes());
			outputStream.close();
		}
		catch(FileNotFoundException fexp)
		{
			System.err.println(fexp.getLocalizedMessage());
		} catch (IOException ioexp) {
			System.err.println(ioexp.getLocalizedMessage());
		}		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
