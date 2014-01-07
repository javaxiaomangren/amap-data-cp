/**
 * 2013-10-17
 */
package roadNet;

import com.vividsolutions.jts.io.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException{
		FindCrossCoord find = new FindCrossCoord();
		find.Testsingle();
	}
}
