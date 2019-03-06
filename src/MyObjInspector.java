import java.util.*;
import java.lang.reflect.*;

public class MyObjInspector {
	
	public MyObjInspector() { } // no need for a constructor
	
	public void inspect (Object object, boolean recursive){
		HashMap objectsToInspect = new HashMap();
		Class classObject = object.getClass();
		
		//inspect name of declaring class
		//inspect name of immediate superclass
		inspectSuperclass(object, classObject);
		//inspect name of interfaces the class implements
		inspectInterfaces(object, classObject);
		//inspect fields the class declares
		inspectFields(object, objectsToInspect, classObject);
		//inspect methods the class declares
		inspectMethods(object, classObject);
		//inspect constructors the class declares
		inspectConstructors(object, classObject);
		
		inspectFieldClasses(object, objectsToInspect, classObject, recursive);
		//inspect field values (only if recursive)
	}
	
	private void inspectFields(Object obj, HashMap objsToInsp, Class classObj){
		if (classObj.getDeclaredFields().length > 0){
			System.out.println("++++++++++++ Getting Fields ++++++++++++");
			Field fields[] = classObj.getDeclaredFields();
			for (Field field : fields){
				field.setAccessible(true);
				if (! field.getType().isPrimitive()){
					objsToInsp.put(field.getName(), field);
				}
				String mods = Modifier.toString(field.getModifiers());
				String val = "";
				try {
					
					if (field.get(obj) != null){
						val = field.get(obj).toString();
					}
				} catch (Exception e) {
					
				}
				System.out.println(mods + " " + field.getName() + " " + val);
			}
				
		}
		if (classObj.getSuperclass() != java.lang.Object.class && classObj.getSuperclass() != null){
			System.out.println("------------------------------------------------------------");
			System.out.println("Inspecting fields of " + classObj.getName() + "'s Superclass: ");
			inspectFields(obj, objsToInsp, classObj.getSuperclass());
			System.out.println("------------------------------------------------------------");
		}
	}
	
	private void inspectFieldClasses(Object obj, HashMap objsToInsp, Class classObj, boolean recursive){

		if (recursive){
			if (!objsToInsp.isEmpty()){
				System.out.println("----- Inspecting Field Classes -----");
			}
			Field fields[] = classObj.getDeclaredFields();
			for(int i = 0; i < fields.length; i++){
				fields[i].setAccessible(true);
				if (!fields[i].getType().isPrimitive()){
					inspect(objsToInsp.get(fields[i].getName()), false);
				}
			}
			
		}
		
		
		
	}
	
	private void inspectSuperclass(Object obj, Class classObj){
		if (classObj.getSuperclass() != null){
			try {
				System.out.println("Name of Superclass: " + classObj.getSuperclass().getName());
			} catch (Exception e) {
				
			}
		}
	}
	
	private void inspectMethods(Object obj, Class classObj){
		if (classObj.getDeclaredMethods().length > 0){
			System.out.println("++++++++++++ Getting methods ++++++++++++");
			Method methods[] = classObj.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++){
				methods[i].setAccessible(true);
				// return types
				String returnType = methods[i].getReturnType().getSimpleName();
				
				// parameter types
				String parameters = "( ";
				Class[] params = methods[i].getParameterTypes();
				if (methods[i].getParameterTypes().length > 0){
					for(Class param : params){
						parameters += param.getSimpleName();
						parameters += " ";
					}
				}
				parameters += ")";
				
				// exceptions
				String exceptions = "";
				Class[] exceps = methods[i].getExceptionTypes();
				if (methods[i].getExceptionTypes().length > 0){
					exceptions += " throws ";
					for (Class excep : exceps){
						exceptions += excep.getSimpleName();
						exceptions += " ";
					}
				}
				// modifiers
				String modifiers = Modifier.toString(methods[i].getModifiers());
				try{
					System.out.println(modifiers + " " + returnType + " " + methods[i].getName() + " " + parameters + exceptions);
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	private void inspectConstructors(Object obj, Class classObj){
		if (classObj.getDeclaredConstructors().length > 0){
			System.out.println("++++++++++++ Getting Constructors ++++++++++++");
			Constructor constructors[] = classObj.getDeclaredConstructors();
			for (Constructor constr : constructors){
				Class[] params = constr.getParameterTypes();
				String parameters = "( ";
				for (Class param : params){
					parameters += param.getSimpleName();
					parameters += " ";
				}
				parameters += ")";
				System.out.println(Modifier.toString(constr.getModifiers()) + " " + constr.getName() + parameters);
			}
		}
	}
	
	private void inspectInterfaces(Object obj, Class classObj){
		if (classObj.getInterfaces().length > 0){
			System.out.println("++++++++++++ Getting Interfaces ++++++++++++");
			Class[] interfaces = classObj.getInterfaces();
			String faces = "";
			for (Class inter : interfaces){
				faces += inter.getSimpleName();
				faces += " ";
			}
			System.out.println("Interfaces: " + faces);
		}
	}
	
	
	
}