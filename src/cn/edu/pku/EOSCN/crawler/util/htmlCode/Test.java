package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class Test {
	private static ASTParser astParser;
	public static void main(String args[]){
		String s = "";
		astParser = ASTParser.newParser(AST.JLS8);
		astParser.setSource(s.toCharArray());
		astParser.setKind(ASTParser.K_STATEMENTS);
		if (s.contains("istData")){
			System.out.println();
		}
		ASTNode ret = (ASTNode) (astParser.createAST(null));
	}
}
