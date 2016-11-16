package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

public class StringEncoders
{
    static public void main(String[] args)
    {
        System.out.println("hex-url: " + hexUrlEncode("a"));
        System.out.println("hex-html: " + hexHtmlEncode("a"));
        System.out.println("decimal-html: " + decimalHtmlEncode("a"));
    }
    static public String hexUrlEncode(String str)   {
        return encode(str, hexUrlEncoder);
    }
    static public String hexHtmlEncode(String str)  {
        return encode(str, hexHtmlEncoder);
    }
    static public String decimalHtmlEncode(String str)  {
        return encode(str, decimalHtmlEncoder);
    }
    static private String encode(String str, CharEncoder encoder)
    {
        StringBuilder buff = new StringBuilder();
        for ( int i = 0; i < str.length(); i++)
            encoder.encode(str.charAt(i), buff);
        return ""+buff;
    }
    public static String decode(String str, CharEncoder encoder){
    	String s = str;
    	for (char c = 0; c < 256; c++){
    		String tmp = Integer.toString(c, 16).toUpperCase();
    		if (tmp.length() < 2) tmp = "0" + tmp;
    		s = s.replace("%"+tmp, ""+c);
    	}
		return s;
    }
    private static class CharEncoder
    {
        String prefix, suffix;
        int radix;
        public CharEncoder(String prefix, String suffix, int radix)        {
            this.prefix = prefix;
            this.suffix = suffix;
            this.radix = radix;
        }
        void encode(char c, StringBuilder buff)     {
            buff.append(prefix).append(Integer.toString(c, radix)).append(suffix);
        }
    }
    public static final CharEncoder hexUrlEncoder = new CharEncoder("%","",16);
    public static final CharEncoder hexHtmlEncoder = new CharEncoder("&#x",";",16);
    public static final CharEncoder decimalHtmlEncoder = new CharEncoder("&#",";",10); 
}
