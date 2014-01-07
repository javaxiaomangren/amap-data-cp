/**
 * @caoxuena
 * 2013-4-10
 *SaveInterfaceTest.java
 */
package com.amap.theaterTest;

import com.amap.theater.tableInterface.SaveInterface;
import org.junit.Test;

public class SaveInterfaceTest {
	@Test
	public void testnameDeal() throws Exception {
		SaveInterface.combineSave("theater_damai_api");
	}
}
