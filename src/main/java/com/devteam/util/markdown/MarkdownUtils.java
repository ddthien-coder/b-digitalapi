package com.devteam.util.markdown;

import com.devteam.util.markdown.ext.cover.CoverExtension;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import com.devteam.util.markdown.ext.heimu.HeimuExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MarkdownUtils {
	private static final Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
	private static final List<Extension> tableExtension = Arrays.asList(TablesExtension.create());
	private static final Set<Extension> taskListExtension = Collections.singleton(TaskListItemsExtension.create());
	private static final Set<Extension> delExtension = Collections.singleton(StrikethroughExtension.create());
	private static final Set<Extension> heimuExtension = Collections.singleton(HeimuExtension.create());
	private static final Set<Extension> coverExtension = Collections.singleton(CoverExtension.create());

	private static final Parser parser = Parser.builder()
			.extensions(tableExtension)
			.extensions(taskListExtension)
			.extensions(delExtension)
			.extensions(heimuExtension)
			.extensions(coverExtension)
			.build();

	private static final HtmlRenderer renderer = HtmlRenderer.builder()
			.extensions(headingAnchorExtensions)
			.extensions(tableExtension)
			.extensions(taskListExtension)
			.extensions(delExtension)
			.extensions(heimuExtension)
			.extensions(coverExtension)
			.attributeProviderFactory(new AttributeProviderFactory() {
				@Override
				public AttributeProvider create(AttributeProviderContext context) {
					return new CustomAttributeProvider();
				}
			})
			.build();

	private static class CustomAttributeProvider implements AttributeProvider {
		@Override
		public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
			if (node instanceof Link) {
				Link n = (Link) node;
				String destination = n.getDestination();
				if (destination.startsWith("#")) {
					attributes.put("class", "toc-link");
				} else {
					//外部链接
					attributes.put("target", "_blank");
					attributes.put("rel", "external nofollow noopener");
				}
			}
			if (node instanceof TableBlock) {
				attributes.put("class", "ui celled table");//针对 semantic-ui 的class属性
			}
		}
	}


	public static String markdownToHtml(String markdown) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdown);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}


	public static String markdownToHtmlExtensions(String markdown) {
		Node document = parser.parse(markdown);
		return renderer.render(document);
	}

	public static void main(String[] args) {
		System.out.println(markdownToHtmlExtensions(""));
	}
}
