import java.util.*;
import java.lang.reflect.*;

public class MyObjInspector {
	
	public MyObjInspector() { } // no need for a constructor
	
	public void inspect (Object object, boolean recursive){
		HashMap objectsToInspect = new HashMap();
		Class classObject = object.getClass();
		
		//inspect name of declaring class
		//inspect name of immediate superclass
		//inspect name of interfaces the class implements
		//inspect methods the class declares
		//inspect constructors the class declares
		//inspect fields the class declares
		inspectFields(object, objectsToInspect, classObject);
		//inspect field values (only if recursive)
	}
	
	private void inspectFields(Object obj, HashMap objsToInsp, Class classObj){
		if (classObj.getDeclaredFields().length > 0){
			Field fields[] = classObj.getDeclaredFields();
			for (int i = 0; i < fields.length; i++){
				fields[i].setAccessible(true);
				try {
					System.out.println("Name of Field " + i + ": " + fields[i].getName() + "has value: " + fields[i].get(obj));
				}
				catch (Exception e){
				}
			}
		}
	}
	
	
}