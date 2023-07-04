package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스로직==//
    //객체지향설계 관점에서 봤을때 필드데이터를 가지고 있는 쪽에 비즈니스 로직이 있는 것이 응집력이 높다.
    //값을 변경할 일이 있으면 setter가 아니라 비지니스 로직을 통해서 변경해야한다.

    /**
     *
     * 재고수량 증가
     * */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * 재고수량 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity -quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more Stock");
        }
        this.stockQuantity =restStock;
    }





}
