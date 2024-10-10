package com.tenco.blog_v1.board;

import com.tenco.blog_v1.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {
   // 네이티브 쿼리연습
    private final BoardNativeRepository boardNativeRepository;
    private final BoardRepository boardRepository;
    private final HttpSession session;

    // JPA , API , JPQL

    // 게시글 수정 기능
    // board/{id}/update
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable(name = "id") Integer id , @ModelAttribute BoardDTO.UpdateDTO reqDTO) {
        // 1. BoardDTO 내에서 내부 클래스로 >> UpdateDTO 생성하기 !
        // 2. 데이터 바인딩 방식 수정 (Model 로 받아오기)
        // 3. (로그인여부) 인증검사 생성
        User sessionUser = (User)session.getAttribute("sessionUser");
        if(sessionUser == null){
            return "redirect:/login-form";
        }

        // 4. 권한체크 (작성자의 글이 맞는지 확인)
        Board board = boardRepository.findById(id);
        // 게시글 없음
        if(board == null){
            return "redirect:/error-404"; // 에러페이지로 보내기
        }
        // 권한 없음
        if(board.getUser().getId().equals(sessionUser.getId()) == false){
            return "redirect:/error-403";
        }

        // 5. 유효성 검사 (생략)

        // 6. 서비스 측 위임 (직접구현) - 레퍼지토리사용
        boardRepository.updateByIdJPA(id, reqDTO.getTitle(), reqDTO.getContent());
        // 7. redirect 처리하기
        return "redirect:/board/" + id;
    }


    // 게시글 수정 화면 요청
    // 주소설계 : http://localhost:8080/board/{id}/update-form
    @GetMapping("board/{id}/update-form")
    public  String updateForm(@PathVariable(name = "id") Integer id , HttpServletRequest request){
        // 1. 게시글 조회
        Board board = boardRepository.findById(id);
        // 2. 요청 속성에 조회한 게시글 속성 및 값 추가
        request.setAttribute("board", board);
        // 뷰 리졸브 - 템플릿 반환 (쿼리문으로 감)
        return  "board/update-form"; // src/main/resources/templates/update-form.mustache
    }

    // 주소설계 http://localhost:8080/board/10/delete ( form 태그 활용이기 때문에 delete 선언 )
    // form 태그에서는 GET, POST 방식만 지원하기 때문.
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id) {

        // 세션에서 로그인한 사용자의 정보를 가져오기 (인증검사)
        User sessionUser = (User)session.getAttribute("sessionUser");

        if(sessionUser == null){
            return  "redirect:/login-form";
        }

        // 권한 체크
        Board board = boardRepository.findById(id);
        if(board == null){
            return "redirect:/error-404"; // 에러페이지로 보내기
        }

        if(!board.getUser().getId().equals((sessionUser.getId()))){
        return "redirect:/error-403";
        }

        boardRepository.deleteByIdJPAAPI(id);
        //boardRepository.deleteById(id);
        //boardNativeRepository.deleteById(id);
        return "redirect:/";
    }



    // 특정 게시글 요청화면
    // 주소설계 : http://localhost:8080/board/1
    @GetMapping("board/{id}")
    public String detail(@PathVariable(name="id")Integer id , HttpServletRequest request) {

        Board board = boardNativeRepository.findById(id);
        request.setAttribute("board" , board);

        return "board/detail";
    }

    @GetMapping("/")
    public String index(Model model) {

        //List<Board> boardList = boardNativeRepository.findAll();
        // 코드 수정
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "index";
    }

    // 주소설계 : http://localhost:8080/board/save-form
    //게시글 작성 화면 연결
    @GetMapping("/board/save-form")
    public String saveForm(){
        return "board/save-form";
    }

    // 게시글 저장
    // 주소설계 - http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String save(BoardDTO.SaveDTO reqDto) {

        User sessionUser =  (User) session.getAttribute("sessionUser");

        if(sessionUser == null) {
            return "redirect:/login-form";
        }
        // 파라미터가 올바르게 전달 되었는지 확인
        log.warn("save 실행: 제목={}, 내용={}", reqDto.getTitle(), reqDto.getContent());

        // boardNativeRepository.save(title, content);
        // SaveDTO 에서 toEntity 사용해서 Board 엔티로 변환하고 인수 값으로 User 정보 정보를 넣었다.
        // 결국 Board 엔티티로 반환이 된다.
        boardRepository.save(reqDto.toEntity(sessionUser));
        return "redirect:/";
    }


}
