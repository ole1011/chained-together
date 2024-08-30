package de.ole101.chained.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.format.TextColor;

import static de.ole101.chained.common.Colors.DARK_PURPLE;
import static de.ole101.chained.common.Colors.GREEN;
import static de.ole101.chained.common.Colors.RED;
import static de.ole101.chained.common.Colors.YELLOW;

@Getter
@RequiredArgsConstructor
public enum Difficulty {

    EASY(7, "Leicht", GREEN),
    MEDIUM(5, "Mittel", YELLOW),
    HARD(4, "Schwer", RED),
    EXTREME(3, "Extrem", DARK_PURPLE);

    private final double chainLength;
    private final String displayName;
    private final TextColor color;
}
