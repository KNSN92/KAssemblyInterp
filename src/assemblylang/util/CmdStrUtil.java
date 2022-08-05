package assemblylang.util;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

public final class CmdStrUtil {
	
	
	/**
	 * 配列に指定した要素が一つだけ含まれているか確認します。
	 * @param list 
	 * @param find
	 * @return 要素が一つだけ含まれていたらtrue。でなければfalse。
	 */
	public static boolean listOneContains(String[] list, String find) {
		if (ArrayUtils.indexOf(list, find) == ArrayUtils.lastIndexOf(list, find)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 配列の中にある要素の数をカウントします。
	 * @param list
	 * @param find
	 * @return カウントした結果
	 */
	public static int listValueCount(String[] list, String find) {
		int valcount = 0;
		for (String listval : list) {
			valcount += listval.equals(find) ? 1 : 0;
		}
		return valcount;
	}
	
	/**
	 * 指定したインデックスを"0"に変更します。
	 * @param list
	 * @param index
	 * @return 変更した配列
	 */
	public static String[] replaceZero(String[] list, int index) {
		Validate.validIndex(list, index);
		String[] clone = ArrayUtils.clone(list);
		clone[index] = "0";
		return clone;
	}
	
	/**
	 * keyWordsで指定した文字をkeyWordsに格納されている要素のインデックスに置き換えます。<br>
	 * <br>
	 * String[] replaceDemo = new String[]{"test1", "test2"};<br>
	 * replaceDemo = replaceKeyWord(replaceDemo, new String[]{"test2"});<br>
	 * result : {"test1", "0"}<br>
	 * 
	 * @param list
	 * @param keyWords
	 * @return 変更した配列
	 */
	public static String[] replaceKeyWord(String[] list, String[] keyWords) {
		String[] clone = ArrayUtils.clone(list);
		int count = 0;
		for (String cloneval : clone) {
			if (ArrayUtils.contains(keyWords, clone)) {
				clone[count] = Integer.toString
					(ArrayUtils.indexOf(keyWords, cloneval));
			}
			count++;
		}
		return clone;
	}
	
	/**
	 * keyWordsで指定した文字をkeyWordsに入っている数値に置き換えます。
	 * @param list
	 * @param keyWords String:置き換えたい文字列。 Integer:置き換える数値
	 * @return 変更した配列
	 */
	public static String[] replaceKeyWord(String[] list, Map<String, Integer> keyWords) {
		String[] clone = ArrayUtils.clone(list);
		int count = 0;
		for (String cloneval : clone) {
			if (keyWords.containsKey(cloneval)) {
				clone[count] = Integer.toString
					(keyWords.get(cloneval));
			}
			count++;
		}
		return clone;
	}
}
