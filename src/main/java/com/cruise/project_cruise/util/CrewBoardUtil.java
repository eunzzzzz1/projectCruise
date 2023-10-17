package com.cruise.project_cruise.util;

import org.springframework.stereotype.Service;

@Service
public class CrewBoardUtil {

	public int getPageCount(int numPerPage, int dataCount) {

		int pageCount = 0;

		pageCount = dataCount / numPerPage;

		if (dataCount % numPerPage != 0) {
			pageCount++;
		}
		
		return pageCount;
	}

	public String pageIndexList(int currentPage, int totalPage, String listUrl) {

		int numperBlock = 5;
		int currentPageSetup;
		int page;

		StringBuffer sb = new StringBuffer();

		if (currentPage == 0 || totalPage == 0) {
			return "";
		}

		if (listUrl.indexOf("?") != -1) {
			listUrl += "&";
		} else {
			listUrl += "?";
		}


		currentPageSetup = (currentPage / numperBlock) * numperBlock;

		if (currentPage % numperBlock == 0) {
			currentPageSetup = currentPageSetup - numperBlock;
		}

		if (totalPage > numperBlock && currentPageSetup > 0) {
			sb.append("<a href=\"" + listUrl + "pageNum=" + currentPageSetup + "\">◀이전</a>&nbsp;");
		}

		page = currentPageSetup + 1;

		while (page <= totalPage && page <= (currentPageSetup + numperBlock)) {

			if (page == currentPage) {
				sb.append("<font color=\"Fuchsia\">" + page + "</font>&nbsp;");
			} else {
				sb.append("<a href=\"" + listUrl + "pageNum=" + page + "\">" + page + "</a>&nbsp;");
			}
			page++;

		}

		if (totalPage - currentPageSetup > numperBlock) {
			sb.append("<a href=\"" + listUrl + "pageNum=" + page + "\">다음▶</a>&nbsp;");
		}

		return sb.toString();
	}



}
