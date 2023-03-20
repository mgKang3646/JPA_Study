package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext // Entity 매니저를 Spring이 알아서 주입해준다.  EntityManager는 PersistenceContext로 주입하고  @Autowired로 주입이 안되지만 주입이 가능하도록 할 예정이다.
    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findOne(Long id){
        return em.find(Member.class,id); // (Type, pk)
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name",Member.class).setParameter("name", name).getResultList();
    }


}
