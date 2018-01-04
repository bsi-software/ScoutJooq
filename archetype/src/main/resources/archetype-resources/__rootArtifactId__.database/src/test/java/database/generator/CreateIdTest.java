#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
package ${package}.database.generator;

import org.junit.Test;

import ${package}.database.table.TableDataInitializer;

public class CreateIdTest {

	@Test
	public void createIds()  throws Exception {
		System.out.println(TableDataInitializer.createId());
		System.out.println(TableDataInitializer.createId());
		System.out.println(TableDataInitializer.createId());
		System.out.println(TableDataInitializer.createId());
	}
}
