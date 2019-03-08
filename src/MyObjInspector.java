import java.util.*;
import java.lang.reflect.*;

public class MyObjInspector {
	
	private static HashMap<String, Class> hash;
	private static Vector<String> seen;
	
	public MyObjInspector() { } // no need for a constructor
	
	public void inspect (Object object, boolean recursive){
		
		System.out.println("\n\n\n");
		
		hash = new HashMap<String, Class>();
		seen = new Vector<String>();
		//HashMap objectsToInspect = new HashMap();
		Class classObject = object.getClass();
		
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
		inspectFields(object, classObject);

		//inspect methods the class declares
		inspectMethods(object, classObject);
		//inspect constructors the class declares
		inspectConstructors(object, classObject);		
		//inspectFieldClasses(object, objectsToInspect, classObject, recursive);
		
		//inspect field values (only if recursive)
		System.out.println("Hashed: \n" + hash.toString());
		System.out.println("Seen: \n" + seen.toString());
		
		for(int i = 0; i < seen.size(); i++){
			String s = seen.get(i).toString();
			Class c = hash.get(s);
			System.out.println("=================================================================================================");
			System.out.println("In class: " + c.getName());
			inspectSuperclass(object, c, recursive);
			//inspect name of interfaces the class implements
			inspectInterfaces(object, c, recursive);
			//inspect fields the class declares
			inspectFields(object, c);

			//inspect methods the class declares
			inspectMethods(object, c);
			//inspect constructors the class declares
			inspectConstructors(object, c);	
			System.out.println("=================================================================================================");
		}
		
		hash.clear();
		seen.clear();
		
		System.out.println("end of inspection of class " + fullName);

	}
	
	private void inspectFields(Object obj, Class classObj){
		System.out.println("++++++++++++ Getting Fields of " + classObj.getSimpleName() + " ++++++++++++");
		if (classObj.getDeclaredFields().length > 0){
			Field fields[] = classObj.getDeclaredFields();
			for (Field field : fields){
				field.setAccessible(true);
				if (! field.getType().isPrimitive()){
					if (!seen.contains(field.getType().getName())){
						seen.add(field.getType().getName());
						hash.put(field.getType().getName(), field.getType());
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
				System.out.println(mods + " " + field.getType().getSimpleName() + " " + field.getName() + " " + val);
			}
				
		}
/*		if (classObj.getSuperclass() != java.lang.Object.class && classObj.getSuperclass() != null){
			System.out.println("------------------------------------------------------------");
			System.out.println("Inspecting fields of " + classObj.getName() + "'s Superclass: ");
			inspectFields(obj, objsToInsp, classObj.getSuperclass());
			System.out.println("------------------------------------------------------------");
		}*/
	}
	
	private void inspectSuperclass(Object obj, Class classObj, boolean recursive){

		if (classObj.getSuperclass() != null){
			System.out.println("------------------- Inspecting Superclass " + classObj.getSuperclass().getName() + "----------------");
			try {
				System.out.println("Name of Superclass: " + classObj.getSuperclass().getName());
				if ( recursive){

					// this is where we would recurse
					// inspect(classObj.getSuperclass(), false);
					// inspect((Object)classObj.getSuperclass().newInstance(), true);
					Class c = classObj.getSuperclass();
					if (!seen.contains(c.getName())){
						seen.add(c.getName());
						hash.put(c.getName(), c);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("------------------- Done Inspecting Superclass ----------------");	
		}
		else {
			System.out.println("No superclass to inspect");
		}
	
	}
	
	private void inspectMethods(Object obj, Class classObj){
		System.out.println("   +++++ Methods of Class " + classObj.getSimpleName() + " ++++++    " );
		System.out.println("this class has " + classObj.getDeclaredMethods().length + " declared methods");
		if (classObj.getDeclaredMethods().length > 0){

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
				if (!inter.getName().contains("java.lang") && recursive){
					if (!seen.contains(inter.getName())){
						hash.put(inter.getName(), inter);
						seen.add(inter.getName());
					}
				}
			}

		}
	}
	
	private void inspectArray(Object obj, boolean recursive){
	}
	
	private void inspectAll(Object object, Class classObject, HashMap objectsToInspect, boolean recursive){

	}
	
	private String getClassName(Class objClass){ return objClass.getSimpleName(); }
	private String getSuperclassName(Class objClass) {return objClass.getSuperclass().getSimpleName(); }
	private Class[] getInterfaces(Class objClass) {return objClass.getInterfaces();}
	private int getClassModifiers(Class objClass) {return objClass.getModifiers();}
	
	
	
	
}