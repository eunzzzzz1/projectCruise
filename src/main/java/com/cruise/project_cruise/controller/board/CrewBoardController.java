package com.cruise.project_cruise.controller.board;

import com.cruise.project_cruise.dto.CrewBoardDTO;
import com.cruise.project_cruise.dto.CrewCommentDTO;
import com.cruise.project_cruise.service.CrewBoardService;
import com.cruise.project_cruise.service.CrewCommentService;
import com.cruise.project_cruise.util.CrewBoardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


@RestController
public class CrewBoardController {

	@Autowired
	private CrewBoardService crewBoardService;
	@Autowired
	private CrewCommentService crewCommentService;

	@Autowired
	CrewBoardUtil myUtil;

	@PostMapping("/board/created")
	public ModelAndView created_ok(CrewBoardDTO dto, @RequestParam("crewNum") int crewNum,
								   HttpServletRequest request) throws Exception {

		HttpSession session = request.getSession();
		String userEmail = (String)session.getAttribute("email");

		ModelAndView mav = new ModelAndView();

		if(userEmail == null) {
			mav.setViewName("redirect:/main");
			return mav;
		}

		//int crewNum = Integer.parseInt(request.getParameter("crewNum"));
		String userName = crewBoardService.getUserName(userEmail);

		int notice = Integer.parseInt(request.getParameter("notice"));

		if (notice == 1) {	// 공지
			dto.setNotice(1);
		} else {	// 일반 게시글
			dto.setNotice(0);
		}

		int maxNum = crewBoardService.maxNum();
		dto.setBoard_num(maxNum + 1);

		dto.setCrew_num(crewNum);
		dto.setEmail(userEmail);
		dto.setName(userName);

		crewBoardService.insertData(dto);

		mav.setViewName("redirect:/board/list?crewNum=" + crewNum);

		return mav;
	}

	@RequestMapping(value = "/board/list", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView list(@RequestParam("crewNum") int crewNum, HttpServletRequest request) throws Exception {

		HttpSession session = request.getSession();
		String userEmail = (String)session.getAttribute("email");

		ModelAndView mav = new ModelAndView();

		if(userEmail == null) {
			mav.setViewName("redirect:/main");
			return mav;
		}

		String pageNum = request.getParameter("pageNum");

		int currentPage = 1;

		if(pageNum != null) {
			currentPage = Integer.parseInt(pageNum);
		}

		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue == null) {
			searchKey = "board_subject";
			searchValue = "";
		} else {
			if(request.getMethod().equalsIgnoreCase("GET")) {
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
		}

		int dataCount = crewBoardService.getDataCount(searchKey, searchValue, crewNum);

		int numPerPage = 10;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if(currentPage > totalPage)
			currentPage = totalPage;

		int start = dataCount - numPerPage * (currentPage - 1);
		int end = dataCount - (currentPage * numPerPage) + 1;

		List<CrewBoardDTO> lists = crewBoardService.getLists(start, end,
				searchKey, searchValue, crewNum);

		String param = "";
		if(searchValue != null && !searchValue.isEmpty()) {
			param += "searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		String listUrl = "/board/list";

		if(!param.isEmpty())
			listUrl = listUrl + "?" + param;

		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);

		String articleUrl ="/board/article?pageNum=" + currentPage;

		if(!param.isEmpty())
			articleUrl = articleUrl + "&" + param;


		mav.setViewName("board/list");

		mav.addObject("crewNum", crewNum);
		mav.addObject("lists", lists);
		mav.addObject("pageIndexList", pageIndexList);
		mav.addObject("dataCount", dataCount);
		mav.addObject("articleUrl", articleUrl);
		mav.addObject("pageNum", currentPage);

		// 게시판 상단 Title-----------------------------------
		Map<String, Object> boardTitle = crewBoardService.boardTitle(crewNum);
		mav.addObject("boardTitle", boardTitle);
		// -----------------------------------게시판 상단 Title

		// 캡틴인 경우 '공지 등록' 가능
		String checkCaptain = crewBoardService.checkCaptain(userEmail);
		mav.addObject("checkCaptain", checkCaptain);


		return mav;
	}

	@RequestMapping(value = "/board/article", method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView article(HttpServletRequest request) throws Exception {

		HttpSession session = request.getSession();
		String userEmail = (String)session.getAttribute("email");

		ModelAndView mav = new ModelAndView();

		if(userEmail == null) {
			mav.setViewName("redirect:/");
			return mav;
		}

		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue != null) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		} else {
			searchKey = "board_subject";
			searchValue = "";
		}

		crewBoardService.updateHitCount(num);	// 조회수 증가

		CrewBoardDTO dto = crewBoardService.getReadData(num);

		if(dto == null) {
			mav.setViewName("redirect:/board/list?pageNum=" + pageNum);

			return mav;
		}

		String param = "pageNum=" + pageNum;
		if(searchValue != null && !searchValue.isEmpty()) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue,"UTF-8");
		}

		mav.addObject("dto", dto);
		mav.addObject("params", param);
		mav.addObject("email", userEmail);

		// 댓글 불러오기------------------------------
		List<CrewCommentDTO> lists = crewCommentService.getLists(dto.getBoard_num());
		mav.addObject("lists", lists);
		// ------------------------------댓글 불러오기

		mav.setViewName("board/article");

		return mav;
	}

	@RequestMapping(value = "/board/updated",
			method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView updated(HttpServletRequest request) throws Exception {

		int num = Integer.parseInt(request.getParameter("board_num"));
		String pageNum = request.getParameter("pageNum");

		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		if(searchValue != null) {
			searchValue = URLDecoder.decode(searchValue, "UTF-8");
		}

		CrewBoardDTO dto = crewBoardService.getReadData(num);

		if(dto == null) {
			ModelAndView mav = new ModelAndView();
			mav.setViewName("redirect:/main");
			return mav;
		}

		String param = "pageNum=" + pageNum;
		if(searchValue != null && !searchValue.isEmpty()) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		ModelAndView mav = new ModelAndView();
		mav.setViewName("board/updated");

		mav.addObject("dto", dto);
		mav.addObject("pageNum", pageNum);
		mav.addObject("params", param);
		mav.addObject("searchKey", searchKey);
		mav.addObject("searchValue", searchValue);

		return mav;
	}

	@RequestMapping(value = "/board/updated_ok",
			method = RequestMethod.POST)
	public ModelAndView updated_ok(CrewBoardDTO dto, HttpServletRequest request) throws Exception {

		HttpSession session = request.getSession();
		String userEmail = (String)session.getAttribute("email");

		ModelAndView mav = new ModelAndView();

		if(userEmail == null) {
			mav.setViewName("redirect:/main");
			return mav;
		}

		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		String param = "pageNum=" + pageNum;

		if(searchValue != null && !searchValue.equals("")) {
			param += "&searchKey=" + searchKey;
			param += "&searchValue=" + URLEncoder.encode(searchValue, "UTF-8");
		}

		crewBoardService.updateData(dto);

		mav.setViewName("redirect:/board/list?" + param);

		return mav;
	}

	@RequestMapping(value = "/board/deleted_ok",
			method = {RequestMethod.POST, RequestMethod.GET})
	public String deleted_ok(HttpServletRequest request) throws Exception {

		int num = Integer.parseInt(request.getParameter("board_num"));

		crewBoardService.deleteData(num);

		return "DeleteSuccess";
	}

}
