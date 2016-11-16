package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.EOSCN.crawler.util.htmlCode.CodeJudge;
import cn.edu.pku.EOSCN.crawler.util.htmlCode.CodeMerge;
import cn.edu.pku.EOSCN.crawler.util.htmlCode.HtmlPage.Segment;


/**
 * @ClassName: CodeClassifier
 * @Description: 识别邮件内容中的代码部分将代码从原始的segment中抽离出来 多个代码段合并成一个代码段
 *               对于每一个邮件段落Segment,若其当前标记是NORMAL_CONTENT 则Judge其是否为代码
 * @author: left
 * @date: 2014年3月5日 上午8:18:56
 */

public class CodeClassifier {

	public static void getClassificationType(HtmlPage e){
		//System.out.println(e.content);
		ArrayList<Segment> segments = e.segments;
//		ArrayList<Segment> tmpsegments = new ArrayList<Segment>(); 
//		for (Segment seg : segments) {
//			if (seg.getContentType() == Segment.NORMAL_CONTENT) {
//				tmpsegments.addAll(CodeJudge.splitCode(seg));
//			}
//		}
//		segments = tmpsegments;
		for (Segment seg : segments) {
			if (seg.getContentType() == Segment.NORMAL_CONTENT) {
				if (CodeJudge.isCode(seg.getContentText())) {
					//if (seg.getSentences().size() < 200)
					seg.setContentType(Segment.CODE_CONTENT);
				}
			}
		}
		ArrayList<Segment> mergedSegment;
		mergedSegment = CodeMerge.continualCodeMerge(segments);
		//mergedSegment = CodeMerge.continualCodeMerge(segments);		
		//mergedSegment = CodeMerge.SplitCodeSegment(mergedSegment);
//		for (Segment seg : mergedSegment) {
//			if (seg.getContentType() == Segment.NORMAL_CONTENT) {
//				if (CodeJudge.isCode(seg.getContentText())) {
//					if (seg.getSentences().size() < 200)
//					seg.setContentType(Segment.CODE_CONTENT);
//				}
//			}
//		}
		//mergedSegment = CodeMerge.SplitCodeSegment(mergedSegment);
		e.segments = mergedSegment;
	}
}
