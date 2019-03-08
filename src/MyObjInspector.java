import java.util.*;
import java.lang.reflect.*;

public class MyObjInspector {
	
	public MyObjInspector() { } // no need for a constructor
	
	public void inspect (Object object, boolean recursive){
		
		System.out.println("into inspection");
		
		HashMap objectsToInspect = new HashMap();
		Class classObject = object.getClass();
		
		inspectAll(object, classObject, objectsToInspect, recursive);

	}
	
	private void inspectFields(Object obj, HashMap objsToInsp, Class classObj){
		if (classObj.getDeclaredFields().length > 0){
			System.out.println("++++++++++++ Getting Fields ++++++++++++");
			Field fields[] = classObj.getDeclaredFields();
			for (Field field : fields){
				field.setAccessible(true);
				if (! field.getType().isPrimitive() && !field.getType().getName().contains("java.lang") && !field.getType().isArray()){
					System.out.println("adding " + field.getName());
					objsToInsp.put(field.getType().getName(), field);
				}
				if (field.getType().isArray()){
					System.out.println("found array");
					Class c = field.getType();
					
					try {
						Object o = field.get(obj);
						for (int i = 0; i < Array.getLength(o) ; i++){
							if(Array.get(o, i) != null){
								System.out.println(Array.get(o, i));
							}
							else {
								System.out.println("null");
							}
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
			System.out.println(objsToInsp.toString());
				
		}
/*		if (classObj.getSuperclass() != java.lang.Object.class && classObj.getSuperclass() != null){
			System.out.println("------------------------------------------------------------");
			System.out.println("Inspecting fields of " + classObj.getName() + "'s Superclass: ");
			inspectFields(obj, objsToInsp, classObj.getSuperclass());
			System.out.println("------------------------------------------------------------");
		}*/
	}
	
	private void inspectFieldClasses(Object obj, HashMap objsToInsp, Class classObj, boolean recursive){

		if (recursive){
			if (!objsToInsp.isEmpty()){
				System.out.println("-------------------- Inspecting Field Classes -----------------------");
				Field fields[] = classObj.getDeclaredFields();
				for (Field field : fields){
					field.setAccessible(true);
					if (objsToInsp.containsKey(field.getType())){
						Class c = field.getType();
						HashMap fieldHash = new HashMap();
						System.out.println(c.getName());
						inspectAll(obj, classObj, fieldHash, recursive);
					}
				
				}
			System.out.println("----------------------- Done Inspecting Field Classes ---------------------");
			}
		}
		
		
		
	}
	
	private void inspectSuperclass(Object obj, Class classObj, boolean recursive){
		if (classObj.getSuperclass() != null){
			try {
				System.out.println("Name of Superclass: " + classObj.getSuperclass().getName());
				if (!classObj.getSuperclass().getName().contains("java.lang") && recursive){
					System.out.println("------------------- Inspecting Superclass ----------------");
					// this is where we would recurse
					// inspect(classObj.getSuperclass(), false);
					// inspect((Object)classObj.getSuperclass().newInstance(), true);
					Class c = classObj.getSuperclass();
					HashMap superHash = new HashMap();
					System.out.println(c.getName());
					inspectAll(obj, classObj, superHash, recursive);
					System.out.println("------------------- Done Inspecting " + classObj.getSuperclass().getName() + " ----------------");
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	
	private void inspectInterfaces(Object obj, Class classObj, boolean recursive){
		if (classObj.getInterfaces().length > 0){
			System.out.println("++++++++++++ Getting Interfaces ++++++++++++");
			Class[] interfaces = classObj.getInterfaces();
			String faces = "";
			for (Class inter : interfaces){
				faces += inter.getSimpleName();
				faces += " ";
			}
			System.out.println("Interfaces: " + faces);
			for (Class inter : interfaces){
				if (!inter.getName().contains("java.io") && !inter.getName().contains("java.lang") && recursive){
					System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Inspecting Heirachal Interfaces =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
					HashMap interHash = new HashMap();
					System.out.println(inter.getName());
					inspectAll(obj, classObj, interHash, recursive);
					System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-    Inspecting Interfaces Done    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
				}
			}

		}
	}
	
	private void inspectArray(Object obj, boolean recursive){
		
	}
	
	private void inspectAll(Object object, Class classObject, HashMap objectsToInspect, boolean recursive){
		String fullName = "";
		fullName += Modifier.toString(getClassModifiers(classObject));
		fullName += " class ";
		fullName += getClassName(classObject);
		if (classObject.getSuperclass() != java.lang.Object.class){
			fullName += " extends ";
			fullName += getSuperclassName(classObject);
			fullName += " ";
		}
		if (classObject.getInterfaces().length > 0){
			fullName += " implements ";
			Class[] interfaces = getInterfaces(classObject);
			for (Class face : interfaces){
				fullName += face.getSimpleName();
				fullName += " ";
			}
		}	
		System.out.println(fullName);
		
		
		//inspect name of declaring class
		//inspect name of immediate superclass
		inspectSuperclass(object, classObject, recursive);
		
		//inspect name of interfaces the class implements
		inspectInterfaces(object, classObject, recursive);
		
		//inspect fields the class declares
		inspectFields(object, objectsToInspect, classObject);
		inspectFieldClasses(object, objectsToInspect, classObject, recursive);
		//inspect methods the class declares
		inspectMethods(object, classObject);
		//inspect constructors the class declares
		inspectConstructors(object, classObject);		
		//inspectFieldClasses(object, objectsToInspect, classObject, recursive);
		
		//inspect field values (only if recursive)
		
		System.out.println("end of inspection of class " + fullName);
	}
	
	private String getClassName(Class objClass){ return objClass.getSimpleName(); }
	private String getSuperclassName(Class objClass) {return objClass.getSuperclass().getSimpleName(); }
	private Class[] getInterfaces(Class objClass) {return objClass.getInterfaces();}
	private int getClassModifiers(Class objClass) {return objClass.getModifiers();}
	
	
	
	
}