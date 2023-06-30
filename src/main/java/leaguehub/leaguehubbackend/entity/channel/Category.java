package leaguehub.leaguehubbackend.entity.channel;

import lombok.Getter;

@Getter
public enum Category {
    TFT(0);

    private int num;

    Category(int num) {
        this.num = num;
    }


    public static Category getByNumber(int number) {
        for (Category category : Category.values()) {
            if (category.num == number) {
                return category;
            }
        }
        return null; // 해당하는 값이 없을 경우 null 반환
    }
}
