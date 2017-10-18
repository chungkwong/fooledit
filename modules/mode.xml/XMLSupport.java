import java.util.regex.*;
public class XMLSupport{
	private static final Pattern XML=Pattern.compile("[Xx][Mm][Ll]");
	private static final Pattern ENC_NAME=Pattern.compile("[A-Za-z][-A-Za-z0-9._]*");
	private static final Pattern VERSION_NUMBER=Pattern.compile("1\\.[0-9]+");
	private static final Pattern PUBLIC_ID=Pattern.compile("[-'()+,./:=?;!*#@$_% \r\na-zA-Z0-9]+");
	private static final Pattern YES_NO=Pattern.compile("yes|no");
	public static boolean isXML(String text){
		return XML.matcher(text).matches();
	}
	public static boolean isEncName(String text){
		return ENC_NAME.matcher(text).matches();
	}
	public static boolean isVersionNumber(String text){
		return VERSION_NUMBER.matcher(text).matches();
	}
	public static boolean isPublicId(String text){
		return PUBLIC_ID.matcher(text).matches();
	}
	public static boolean isYesNo(String text){
		return YES_NO.matcher(text).matches();
	}
}
