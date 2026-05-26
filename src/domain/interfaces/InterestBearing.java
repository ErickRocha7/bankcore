package domain.interfaces;

/**
 * Interface para objetos que podem render juros.
 *
 * Capítulo 10 – Programação orientada a objetos: polimorfismo e interfaces.
 */
public interface InterestBearing {
    /**
     * Retorna a taxa de juros anual (em percentual).
     * 
     * @return taxa de juros (ex.: 5.0 para 5% a.a.)
     */
    double getInterestRate();

    /**
     * Calcula e exibe o rendimento ao longo de um número de anos.
     * 
     * @param years número de anos para projeção
     */
    void calculateInterest(int years);
}