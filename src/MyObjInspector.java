import java.util.*;
import java.lang.reflect.*;

public class MyObjInspector {
	
	private static HashMap<String, Class> hash;
	private static Vector<String> seen;
	
	public MyObjInspector() { } // no need for a constructor
	
	public void inspect (Object object, boolean recursive){
		
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
		
		if (classObject.isArray()){
			inspectArray(object, classObject, recursive);
			printArray(object, recursive);
		}

		//inspect name of immediate superclass
		inspectSuperclass(classObject, recursive);
		//inspect name of interfaces the class implements
		inspectInterfaces(classObject, recursive);
		//inspect fields the class declares
		inspectFields(object, classObject, recursive);
		//inspect methods the class declares
		inspectMethods(classObject);
		//inspect constructors the class declares
		inspectConstructors(classObject);		
		//inspectFieldClasses(object, objectsToInspect, classObject, recursive);
		
		for(int i = 0; i < seen.size(); i++){
			String s = seen.get(i).toString();
			Class c = hash.get(s);
			System.out.println("======");
			System.out.println("In class: " + c.getName());
			inspectSuperclass(c, recursive);
			//inspect name of interfaces the class implements
			inspectInterfaces(c, recursive);
			//inspect fields the class declares
			inspectFields(object, c, recursive);
			//inspect methods the class declares
			inspectMethods(c);
			//inspect constructors the class declares
			inspectConstructors(c);	
			System.out.println("======");
		}
		
		hash.clear();
		seen.clear();
		
		System.out.println("end of inspection of class " + fullName);

	}
	
	private void inspectFields(Object obj, Class classObj, boolean recursive){
		System.out.println("++++++++++++ Getting Fields of " + classObj.getSimpleName() + " ++++++++++++");
		if (classObj.getDeclaredFields().length > 0){
			Field fields[] = classObj.getDeclaredFields();
			for (Field field : fields){
				field.setAccessible(true);
				if (! field.getType().isPrimitive()){
					if (!seen.contains(field.getType().getName())){
						if(field.getType().isArray()){
							try {
								inspectArray(field.get(obj), field.getType(), recursive);
								printArray(field.get(obj), recursive);
							} catch (IllegalArgumentException | IllegalAccessException e) {
								System.out.println("Caught an illegal exception with arrays");
							}
						}
						else {
							seen.add(field.getType().getName());
							hash.put(field.getType().getName(), field.getType());
						}
					}
				}

				String mods = Modifier.toString(field.getModifiers());
				String val = "";
				try {
					if (field.get(obj) != null){
						if (recursive){
							val = field.get(obj).toString();
							System.out.println(mods + " " + field.getType().getSimpleName() + " " + field.getName() + " " + val);
						}
						else {
							int hashcode = field.get(obj).hashCode();
							System.out.println(mods + " " + field.getType().getSimpleName() + " " + field.getName() + " " + hashcode);
						}
					}
				} catch (Exception e) {
					
				}

			}		
		}
	}
	
	private void inspectSuperclass(Class classObj, boolean recursive){

		if (classObj.getSuperclass() != null){
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
		}
		else {
			System.out.println("No superclass to inspect");
		}
	
	}
	
	private void inspectMethods(Class classObj){
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
	
	private void inspectConstructors(Class classObj){
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
	
	private void inspectInterfaces(Class classObj, boolean recursive){
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
	
	private void inspectArray(Object obj, Class objClass, boolean recursive){
		if(obj.getClass() != null){
			try{
				if (!seen.contains(obj.getClass().getComponentType().getName())){
					hash.put(obj.getClass().getComponentType().getName(), obj.getClass().getComponentType());
					seen.add(obj.getClass().getComponentType().getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printArray(Object obj, boolean recursive){
		int arrayLength = Array.getLength(obj);
		String arrayString = "[ ";
		for (int i = 0; i < arrayLength; i++){
			if (Array.get(obj, i) != null){
				Object o = Array.get(obj, i);
				Class c = o.getClass();
				arrayString += c.getSimpleName() + " ";
				if (o.getClass().isArray()){
					printArray(o, recursive);
				}
			}
			else {
				arrayString += "null ";
			}
		}
		arrayString += "]";
		System.out.println(arrayString);
	}
	
	private String getClassName(Class objClass){ return objClass.getSimpleName(); }
	private String getSuperclassName(Class objClass) {return objClass.getSuperclass().getSimpleName(); }
	private Class[] getInterfaces(Class objClass) {return objClass.getInterfaces();}
	private int getClassModifiers(Class objClass) {return objClass.getModifiers();}
	
	
	
	
}