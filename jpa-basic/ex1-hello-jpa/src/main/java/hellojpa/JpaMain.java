package hellojpa;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        //persistence.xml에서  <persistence-unit name="hello"> 참조
        //DB당 하나만 존재
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        //고객의 요청이 올때마다 썼다가 버렸다가 하는거... 절대 쓰레드간에 공유를 해서는 안 된다.
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx =em.getTransaction();
        tx.begin();
        //JPA의 모든 변경작업은 트랜잭션 안에서 이루어져야 한다!!
        try{
           // Member findMember = em.find(Member.class, 1L);
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(5)
                    .setMaxResults(8)
                    .getResultList();

            for(Member member : result){
                System.out.println("member = " + member.getName());
            }

            tx.commit();

        }catch (Exception e){
            tx.rollback();
        }finally{
            em.clear();
        }
        emf.close();
    }
}
