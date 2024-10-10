package com.tenco.blog_v1.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository // IoC
public class BoardRepository {

    private final EntityManager em;

    // 1. JPQL 로 객체지향 쿼리를 이용하기 , JPA API 사용하기
    @Transactional
    public void updateByIdJPQL(int id , String title , String content){
        // JPQL
        String jpql = " UPDATE Board b SET b.title , b.content = :content WHERE b.id = :id ";
        Query query = em.createQuery(jpql);
        query.setParameter("title" , title);
        query.setParameter("content" , content);
        query.setParameter("id" , id);
        query.executeUpdate();
    }

    // JPA
    public void  updateByIdJPA(int id , String title , String content){
        Board board = em.find(Board.class , id);
        if(board != null){
            board.setTitle(title);
            board.setContent(content);
        }
    }


    /**
     * 게시글 조회 메서드
     * @param id 조회할 게시글 ID
     * @return 조회된 Board 엔티티, 존재하지 않으면 null 반환
     */
    public Board findById(int id) {
        return em.find(Board.class, id);
    }

    /**
     * JPQL의 FETCH 조인 사용 - 성능 최적화
     * 한방에 쿼리를 사용해서 (즉, 직접 조인해서) 데이터를 가져온다.
     * @param id
     * @return
     */
    public Board findByIdJoinUser(int id) {
        // JPQL -> Fetch join을 사용해보자.
        String jpql = " SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :id ";
        return em.createQuery(jpql, Board.class).setParameter("id", id).getSingleResult();
    }

    /**
     * 모든 게시글 조회
     * @return
     */
    public List<Board> findAll() {
        TypedQuery<Board> jpql = em.createQuery(" SELECT b FROM Board b ORDER BY b.id DESC ", Board.class);
        return jpql.getResultList();
    }
    // em.persist(board) -> 비 영속 상태인 엔티티를 영속상태로 전환
     @Transactional
    public  Board save( Board board){
        em.persist(board);
        return board;
    }

    /**
     * 게시글 삭제하기
     * @param id
     */
    @Transactional // 트랜잭션 내에서 실행되도록 보장
    public void deleteById(int id){
        Query jpql = em.createQuery(" DELETE FROM Board b WHERE b.id = :id ");
        jpql.setParameter("id",id);
        jpql.executeUpdate();
    }




    @Transactional
    public void  deleteByIdJPAAPI(int id){
        em.remove(em.find(Board.class, id));
    }


}
