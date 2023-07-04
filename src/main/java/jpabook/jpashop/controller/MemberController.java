package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm",new MemberForm());
        return "members/createMemberForm";
    }

    //Member member가 아닌 MemberForm 을 하나 추가하는 이유는 member도메인과 MemberForm 사이의 역할이 분리할 필요가 있기 때문이다. 실무 Form은 정말 복잡하다.
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){ //@Valid form의 유효성검사, => 에러가 있으면 result에 담기고 내려간다.

        if(result.hasErrors()){  //MemberForm 에서 @NotEmpty의 메시지가 출력된다.
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";

    }

    @GetMapping("/members")
    public String list(Model model){
        model.addAttribute("members", memberService.findMember());
        return "members/memberList";
    }

}
