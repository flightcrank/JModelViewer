
/**
 *
 * @author karma
 */
public class Test {
	
	public static void main(String[] args) {
		
		
		ObjParser obj = new ObjParser();
		
		try {
			obj.parseFile("test.obj");
			float a[] = obj.model.get(0).normalsToArray();
			int i = 1;
			
		} catch(Exception ex) {
			
			System.out.println(ex.getMessage());
			
		}
	}
}
