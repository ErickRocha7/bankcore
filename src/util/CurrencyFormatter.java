package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utilitário para formatação e parsing de valores monetários no padrão
 * brasileiro.
 *
 * Capítulos abordados:
 * 14 - Strings, formatação e expressões regulares
 * 16 - Coleções e utilitários
 */
public final class CurrencyFormatter {

    private static final DecimalFormat FORMATTER;

    static {
        // Usando fábrica moderna (não depreciada) – Java 19+
        Locale ptBR = Locale.of("pt", "BR");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(ptBR);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        FORMATTER = new DecimalFormat("#,##0.00", symbols);
        FORMATTER.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    private CurrencyFormatter() {
        // classe utilitária
    }

    /**
     * Formata um BigDecimal para exibição no padrão brasileiro.
     * Exemplo: 1500.50 -> "R$ 1.500,50"
     *
     * @param value valor a formatar
     * @return string formatada
     */
    public static String format(BigDecimal value) {
        if (value == null)
            return "R$ 0,00";
        return "R$ " + FORMATTER.format(value);
    }

    /**
     * Converte uma string de entrada do usuário para BigDecimal.
     * Aceita tanto vírgula quanto ponto como separador decimal,
     * e também pontos de agrupamento de milhar.
     *
     * Exemplos válidos: "1500,50", "1.500,50", "1500.50"
     *
     * @param input string de entrada
     * @return BigDecimal correspondente
     * @throws NumberFormatException se a string não for numérica
     */
    public static BigDecimal parse(String input) {
        if (input == null || input.isBlank()) {
            throw new NumberFormatException("Valor vazio.");
        }
        // Remove pontos de milhar e substitui vírgula por ponto
        String normalized = input.trim().replace(".", "").replace(",", ".");
        // Se houver mais de um ponto decimal, é inválido
        if (normalized.chars().filter(ch -> ch == '.').count() > 1) {
            throw new NumberFormatException("Formato inválido: " + input);
        }
        return new BigDecimal(normalized);
    }
}