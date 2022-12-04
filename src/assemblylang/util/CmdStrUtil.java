package assemblylang.util;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public final class CmdStrUtil {

	/**
	 * 配列に指定した要素が一つだけ含まれているか確認します。
	 * @param list 
	 * @param find
	 * @return 要素が一つだけ含まれていたらtrue。でなければfalse。
	 */
	public static boolean listOneContains(String[] list, String find) {
		if (ArrayUtils.contains(list,find)&ArrayUtils.indexOf(list, find) == ArrayUtils.lastIndexOf(list, find)) {
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
				clone[count] = Integer.toString(ArrayUtils.indexOf(keyWords, cloneval));
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
				clone[count] = Integer.toString(keyWords.get(cloneval));
			}
			count++;
		}
		return clone;
	}

	/**
	 * keyWordsで指定した文字列のいずれかがlistに含まれているかを調べます。
	 * @param list 調べる配列
	 * @param keyWords 調べる文字列
	 * @return listにkeyWordsのいずれかが含まれているならtrue。でなければfalse。
	 */
	public static boolean containsAny(String[] list, String[] keyWords) {
		for (String listVal : list) {
			if (isSameAnyWord(listVal, keyWords)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * keyWordsで指定した文字列のいずれかがstrに含まれているかを調べます。
	 * @param str 調べる文字列
	 * @param keyWords 調べる文字列
	 * @return strにkeyWordsのいずれかが含まれているならtrue。でなければfalse。
	 */
	public static boolean isSameAnyWord(String str, String[] keyWords) {
		return ArrayUtils.contains(keyWords, str);
	}

	/**
	 * matchWordsで指定した構成になっているか調べます。
	 * @param list 調べる配列
	 * @param matchWords 調べるのに使う配列
	 * @return matchWordsで指定した構成になっていればtrue。
	 */
	public static boolean isSameAll(String[] list, String[][] matchWords) {
		Validate.notNull(list);
		Validate.notNull(matchWords);
		int index = 0;
		for (String[] words : matchWords) {
			for (String word : words) {
				if (!list[index].matches(word)) {
					return false;
				}
			}
			index++;
		}
		return true;
	}

	/**
	 * 引数の配列の中身の文字列を全て大文字に変換します。
	 * @param strs 大文字にする配列
	 * @return 大文字にした引数。
	 */
	public static String[] toAllUpperCase(String[] strs) {
		String[] outs = strs;
		for (int i = 0; i < strs.length; i++) {
			outs[i] = outs[i].toUpperCase();
		}
		return outs;
	}

	/**
	 * 引数の配列の中身の文字列から、正規表現にマッチする最初のインデックスを返します。
	 * @param args
	 * @param regex
	 * @return index
	 */
	
	public static int indexOfRegex(String[] args, String regex) {
		Validate.noNullElements(args);
		Validate.notNull(regex);
		for(int i=0;i<args.length;i++) {
			if(args[i].matches(regex)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 引数の配列の中身の文字列に、正規表現にマッチする物があるか調べます。
	 * @param args
	 * @param regex
	 * @return result
	 */
	
	public static boolean containsRegex(String[] args, String regex) {
		return CmdStrUtil.indexOfRegex(args, regex) != -1;
	}
	
	/**
	 * StringUtils.isBlackメソッドを使用して、空文字が入っている配列の最初のインデックスを返します。
	 * @param args
	 * @return result
	 */
	public static int indexOfBlank(String[] args) {
		Validate.noNullElements(args);
		for(int i=0;i<args.length;i++) {
			if(StringUtils.isBlank(args[i])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * StringUtils.isBlackメソッドを使用して、空文字が入っているかを確認します。
	 * @param args
	 * @return result
	 */
	public static boolean containsBlank(String[] args) {
		return CmdStrUtil.indexOfBlank(args) != -1;
	}
}
