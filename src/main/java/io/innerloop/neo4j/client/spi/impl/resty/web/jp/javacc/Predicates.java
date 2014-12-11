package io.innerloop.neo4j.client.spi.impl.resty.web.jp.javacc;

import io.innerloop.neo4j.client.json.JSONException;
import io.innerloop.neo4j.client.json.JSONObject;

import java.util.List;


public class Predicates {
	public interface Test {
		boolean test(JSONObject json) throws JSONException;
	}

	static class Operator implements Test {
		JSONPathCompiler.JSONPathExpr lhs;
		char ops;
		JSONPathCompiler.JSONPathExpr rhs;

		Operator(JSONPathCompiler.JSONPathExpr aSubExpr, String anOperator, JSONPathCompiler.JSONPathExpr aValue) {
			lhs = aSubExpr;
			ops = anOperator.charAt(0);
			rhs = aValue;
		}

		@Override
		public String toString() {
			return "" + lhs + " " + ops + " " + rhs;
		}

		public boolean test(JSONObject json) throws JSONException {
			Object eval = lhs.eval(json);
			if (eval instanceof List) {
				List results = (List) eval;
				for (Object o : results) {
					boolean result = compare(json, (Comparable) o); // if any object from the lhs matches, good!
					if (result) {
						return true;
					}
				}
			} else {
				return compare(json, (Comparable) eval);
			}
			return false;
		}

		private boolean compare(JSONObject json, Comparable val) throws JSONException {
			if (val instanceof Number) { // fix comparison between Integers and Doubles by making sure the extracted value is a double
				val = ((Number) val).doubleValue();
			}
			if (rhs == null) { // test for existence of an attribute
				return json.has(val.toString());
			} else {
				Comparable rhsVal = (Comparable) rhs.eval(json); // TODO might also be a List!
				int comparisonResult = val.compareTo(rhsVal);
				switch (ops) {
				case '>':
					return comparisonResult > 0;
				case '=':
					return comparisonResult == 0;
				case '<':
					return comparisonResult < 0;
				}
				return false;
			}
		}
	}

	static class Existence implements Test {

		@Override
		public boolean test(JSONObject json) throws JSONException {
			//
			return false;
		}

	}

	static class Identity implements Test {
		Test child;

		Identity(Test aChild) {
			child = aChild;
		}

		public boolean test(JSONObject json) throws JSONException {
			return child.test(json);
		}

	}
}
