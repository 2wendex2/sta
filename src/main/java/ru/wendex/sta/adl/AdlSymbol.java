package ru.wendex.sta.adl;

/**
 * Символ алфавита автомата ADL
 */
public interface AdlSymbol {
	/**
	 * Возвращает местность символа
	 * @return местность
	 */
	int getArity();
	
	/**
	 * Возвращает строковое представление символа
	 * @return строковое представление символа
	 */
	String toString();
}
