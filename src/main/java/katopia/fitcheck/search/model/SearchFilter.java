package katopia.fitcheck.search.model;

import katopia.fitcheck.member.domain.Gender;

public record SearchFilter(
        Short minHeight,
        Short maxHeight,
        Short minWeight,
        Short maxWeight,
        Gender gender
) {
    public boolean isEmpty() {
        return minHeight == null && minWeight == null && gender == null;
    }
}
