package com.dtc.common.util.sort;

import java.util.List;

interface Sortable {
	 <T extends Comparable<T>> List<T> sort();
}
