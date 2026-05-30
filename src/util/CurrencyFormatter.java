package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utilitário para formatação e parsing de valores monetários no padrão
 * brasileiro. Agora exige o formato rigoroso com vírgula como separador
 * decimal e ponto como separador de milhar (opcional).
 *
 * Exemplos válidos: "1500,00", "1.500,00", "1500,50"
 * Formatos inválidos: "1500.00", "1,500.00", "1.500"
 */
public final class CurrencyFormatter {

    private static final DecimalFormat FORMATTER;

    static {
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
     */
    public static String format(BigDecimal value) {
        if (value == null)
            return "R$ 0,00";
        return "R$ " + FORMATTER.format(value);
    }

    /**
     * Converte uma string de entrada para BigDecimal usando o formato brasileiro
     * estrito.
     * Aceita apenas strings que atendam ao padrão: dígitos com vírgula como
     * separador
     * decimal e, opcionalmente, pontos separando milhares (ex: "1.500,00" ou
     * "1500,00").
     *
     * Qualquer desvio (uso de ponto como decimal, ausência de centavos, etc.) gera
     * uma exceção descritiva.
     *
     * @param input string de entrada fornecida pelo usuário
     * @return BigDecimal correspondente
     * @throws IllegalArgumentException se o formato for inválido
     */
    public static BigDecimal parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Valor vazio. Digite um número no formato 1.500,00.");
        }

        String trimmed = input.trim();

        // Valida o formato: aceita dígitos, pontos de milhar e exatamente uma vírgula
        // decimal
        if (!trimmed.matches("^\\d{1,3}(\\.\\d{3})*,\\d{2}$")) {
            throw new IllegalArgumentException(
                    "Formato inválido: \"" + trimmed
                            + "\". Use o padrão brasileiro: 1.500,00 (vírgula para centavos, ponto para milhar opcional).");
        }

        // Remove pontos de milhar e substitui vírgula decimal por ponto
        String normalized = trimmed.replace(".", "").replace(",", ".");
        return new BigDecimal(normalized);
    }
}