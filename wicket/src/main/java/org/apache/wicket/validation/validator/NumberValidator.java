/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.validation.validator;

import java.util.Map;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.validation.IValidatable;

/**
 * Validator for checking numbers. Use the static factory methods to make range/minimum or maximum
 * validators for <code>double</code>s or <code>long</code>s.
 * 
 * @deprecated {@link org.apache.wicket.validation.validator.RangeValidator},
 *             {@link org.apache.wicket.validation.validator.MaximumValidator},
 *             {@link org.apache.wicket.validation.validator.MinimumValidator}
 * 
 * @author Jonathan Locke
 * @author Johan Compagner
 * @author Igor Vaynberg (ivaynberg)
 * @param <T>
 *            type of validatable
 * @since 1.2.6
 * 
 *        FIXME remove after 1.4
 */
@Deprecated
@SuppressWarnings("unchecked")
public abstract class NumberValidator<T extends Number> extends AbstractValidator<T>
{
	private static final long serialVersionUID = 1L;

	/**
	 * a validator for ensuring for a positive number value (>0 so not including 0)
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.MinimumValidator}
	 */
	@Deprecated
	public static final NumberValidator POSITIVE = new DoubleMinimumValidator(Double.MIN_VALUE)
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected String resourceKey()
		{
			return "NumberValidator.positive";
		}
	};

	/**
	 * a validator for ensuring a negative number value (<0 so not including 0)
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.MaximumValidator}
	 */
	@Deprecated
	public static final NumberValidator NEGATIVE = new DoubleMaximumValidator(-Double.MIN_VALUE)
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected String resourceKey()
		{
			return "NumberValidator.negative";
		}
	};

	/**
	 * Gets an Integer range validator for checking if a number falls between the minimum and
	 * maximum values. If that is not the case, an error message will be generated with the key
	 * "RangeValidator". The message keys that can be used are:
	 * <p>
	 * <ul>
	 * <li>${minimum}: the minimum value</li>
	 * <li>${maximum}: the maximum value</li>
	 * <li>${input}: the input the user gave</li>
	 * <li>${name}: the name of the <code>Component</code> that failed</li>
	 * <li>${label}: the label of the <code>Component</code> - either comes from
	 * <code>FormComponent.labelModel</code> or resource key [form-id].[form-component-id] in that
	 * order</li>
	 * </ul>
	 * 
	 * @param minimum
	 *            the minimum value
	 * @param maximum
	 *            the maximum value
	 * 
	 * @return the request <code>NumberValidator</code>
	 * @deprecated see {@link org.apache.wicket.validation.validator.RangeValidator}
	 */
	@Deprecated
	public static NumberValidator range(long minimum, long maximum)
	{
		return new RangeValidator(minimum, maximum);
	}

	/**
	 * Gets an Integer minimum validator for checking if a number is greater than or equal to the
	 * given minimum value. If that is not the case, an error message will be generated with the key
	 * "MinimumValidator". The message keys that can be used are:
	 * <p>
	 * <ul>
	 * <li>${minimum}: the minimum value</li>
	 * <li>${input}: the input the user gave</li>
	 * <li>${name}: the name of the <code>Component</code> that failed</li>
	 * <li>${label}: the label of the <code>Component</code> - either comes from
	 * <code>FormComponent.labelModel</code> or resource key [form-id].[form-component-id] in that
	 * order</li>
	 * </ul>
	 * 
	 * @param minimum
	 *            the minimum value
	 * 
	 * @return the requested <code>NumberValidator</code>
	 * @deprecated see {@link org.apache.wicket.validation.validator.MinimumValidator}
	 */
	@Deprecated
	public static NumberValidator minimum(long minimum)
	{
		return new MinimumValidator(minimum);
	}

	/**
	 * Gets an Integer range validator for checking if a number is smaller than or equal to the
	 * given maximum value. If that is not the case, an error message will be generated with the key
	 * "MaximumValidator". The message keys that can be used are:
	 * <p>
	 * <ul>
	 * <li>${maximum}: the maximum value</li>
	 * <li>${length}: the length of the user input</li>
	 * <li>${input}: the input the user gave</li>
	 * <li>${name}: the name of the <code>Component</code> that failed</li>
	 * <li>${label}: the label of the <code>Component</code> - either comes from
	 * <code>FormComponent.labelModel</code> or resource key [form-id].[form-component-id] in that
	 * order</li>
	 * </ul>
	 * 
	 * @param maximum
	 *            the maximum value
	 * 
	 * @return the requested <code>NumberValidator</code>
	 * @deprecated see {@link org.apache.wicket.validation.validator.MaximumValidator}
	 */
	@Deprecated
	public static NumberValidator maximum(long maximum)
	{
		return new MaximumValidator(maximum);
	}

	/**
	 * Gets a Double range validator for checking if a number falls between (including) the minimum
	 * and maximum values. If that is not the case, an error message will be generated with the key
	 * "RangeValidator". The message keys that can be used are:
	 * <p>
	 * <ul>
	 * <li>${minimum}: the minimum value</li>
	 * <li>${maximum}: the maximum value</li>
	 * <li>${input}: the input the user gave</li>
	 * <li>${name}: the name of the <code>Component</code> that failed</li>
	 * <li>${label}: the label of the <code>Component</code> - either comes from
	 * <code>FormComponent.labelModel</code> or resource key [form-id].[form-component-id] in that
	 * order</li>
	 * </ul>
	 * 
	 * @param minimum
	 *            the minimum value
	 * @param maximum
	 *            the maximum value
	 * 
	 * @return the requested <code>NumberValidator</code>
	 * @deprecated see {@link org.apache.wicket.validation.validator.RangeValidator}
	 */
	@Deprecated
	public static NumberValidator range(double minimum, double maximum)
	{
		return new DoubleRangeValidator(minimum, maximum);
	}

	/**
	 * Gets a Double minimum validator for checking if number is greater then or equal to the given
	 * minimum value. If that is not the case, an error message will be generated with the key
	 * "MinimumValidator". The message keys that can be used are:
	 * <p>
	 * <ul>
	 * <li>${minimum}: the minimum value</li>
	 * <li>${input}: the input the user gave</li>
	 * <li>${name}: the name of the <code>Component</code> that failed</li>
	 * <li>${label}: the label of the <code>Component</code> - either comes from
	 * <code>FormComponent.labelModel</code> or resource key [form-id].[form-component-id] in that
	 * order</li>
	 * </ul>
	 * 
	 * @param minimum
	 *            the minimum value
	 * 
	 * @return the requested <code>NumberValidator</code>
	 * @deprecated see {@link org.apache.wicket.validation.validator.MinimumValidator}
	 */
	@Deprecated
	public static NumberValidator minimum(double minimum)
	{
		return new DoubleMinimumValidator(minimum);
	}

	/**
	 * Gets a Double maximum validator for checking if an number is smaller than or equal to the
	 * given maximum value. If that is not the case, an error message will be generated with the key
	 * "MaximumValidator". The message keys that can be used are:
	 * <p>
	 * <ul>
	 * <li>${maximum}: the maximum value</li>
	 * <li>${input}: the input the user gave</li>
	 * <li>${name}: the name of the <code>Component</code> that failed</li>
	 * <li>${label}: the label of the <code>Component</code> - either comes from
	 * <code>FormComponent.labelModel</code> or resource key [form-id].[form-component-id] in that
	 * order</li>
	 * </ul>
	 * 
	 * @param maximum
	 *            the maximum value
	 * 
	 * @return the requested <code>NumberValidator</code>
	 * @deprecated see {@link org.apache.wicket.validation.validator.MaximumValidator}
	 */
	@Deprecated
	public static NumberValidator maximum(double maximum)
	{
		return new DoubleMaximumValidator(maximum);
	}

	/**
	 * Validator for checking if a given number is within the specified range.
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.RangeValidator}
	 */
	@Deprecated
	public static class RangeValidator extends NumberValidator
	{
		private static final long serialVersionUID = 1L;
		private final long minimum;
		private final long maximum;

		/**
		 * Constructor that sets the minimum and maximum values.
		 * 
		 * @param minimum
		 *            the minimum value
		 * @param maximum
		 *            the maximum value
		 */
		public RangeValidator(long minimum, long maximum)
		{
			this.minimum = minimum;
			this.maximum = maximum;

		}

		/**
		 * @see AbstractValidator#variablesMap(IValidatable)
		 */
		@Override
		protected Map<String, Object> variablesMap(IValidatable validatable)
		{
			final Map<String, Object> map = super.variablesMap(validatable);
			map.put("minimum", new Long(minimum));
			map.put("maximum", new Long(maximum));
			return map;
		}

		/**
		 * @see AbstractValidator#resourceKey(FormComponent)
		 */
		@Override
		protected String resourceKey()
		{
			return "RangeValidator";
		}

		/**
		 * @see AbstractValidator#onValidate(IValidatable)
		 */
		@Override
		protected void onValidate(IValidatable validatable)
		{
			Number value = (Number)validatable.getValue();
			if (value.longValue() < minimum || value.longValue() > maximum)
			{
				error(validatable);
			}
		}

		/**
		 * Gets the minimum value.
		 * 
		 * @return minimum value
		 */
		public long getMinimum()
		{
			return minimum;
		}

		/**
		 * Gets the maximum value.
		 * 
		 * @return maximum value
		 */
		public long getMaximum()
		{
			return maximum;
		}
	}

	/**
	 * Validator for checking if a given number number meets the minimum requirement.
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.MinimumValidator}
	 */
	@Deprecated
	public static class MinimumValidator extends NumberValidator
	{
		private static final long serialVersionUID = 1L;
		private final long minimum;

		/**
		 * Constructor that sets the minimum value.
		 * 
		 * @param minimum
		 *            the minimum value
		 */
		public MinimumValidator(long minimum)
		{
			this.minimum = minimum;
		}

		/**
		 * @see AbstractValidator#variablesMap(IValidatable)
		 */
		@Override
		protected Map<String, Object> variablesMap(IValidatable validatable)
		{
			final Map<String, Object> map = super.variablesMap(validatable);
			map.put("minimum", new Long(minimum));
			return map;
		}

		/**
		 * @see AbstractValidator#resourceKey(FormComponent)
		 */
		@Override
		protected String resourceKey()
		{
			return "MinimumValidator";
		}

		/**
		 * @see AbstractValidator#onValidate(IValidatable)
		 */
		@Override
		protected void onValidate(IValidatable validatable)
		{
			if (((Number)validatable.getValue()).longValue() < minimum)
			{
				error(validatable);
			}

		}

		/**
		 * Gets the minimum.
		 * 
		 * @return minimum
		 */
		public long getMinimum()
		{
			return minimum;
		}
	}

	/**
	 * Validator for checking if a given number meets the maximum requirement.
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.MaximumValidator}
	 */
	@Deprecated
	public static class MaximumValidator extends NumberValidator
	{
		private static final long serialVersionUID = 1L;
		private final long maximum;

		/**
		 * Constructor that sets the maximum value.
		 * 
		 * @param maximum
		 *            the maximum value
		 */
		public MaximumValidator(long maximum)
		{
			this.maximum = maximum;
		}

		/**
		 * @see AbstractValidator#variablesMap(IValidatable)
		 */
		@Override
		protected Map<String, Object> variablesMap(IValidatable validatable)
		{
			final Map<String, Object> map = super.variablesMap(validatable);
			map.put("maximum", new Long(maximum));
			return map;
		}

		/**
		 * @see AbstractValidator#resourceKey(FormComponent)
		 */
		@Override
		protected String resourceKey()
		{
			return "MaximumValidator";
		}

		/**
		 * @see AbstractValidator#onValidate(IValidatable)
		 */
		@Override
		protected void onValidate(IValidatable validatable)
		{
			if (((Number)validatable.getValue()).longValue() > maximum)
			{
				error(validatable);
			}
		}

		/**
		 * Gets the maximum.
		 * 
		 * @return maximum
		 */
		public long getMaximum()
		{
			return maximum;
		}
	}

	/**
	 * Validator for checking if a given <code>double</code> is within the specified range.
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.RangeValidator}
	 */
	@Deprecated
	public static class DoubleRangeValidator extends NumberValidator
	{
		private static final long serialVersionUID = 1L;
		private final double minimum;
		private final double maximum;

		/**
		 * Constructor that sets the maximum and minimum values.
		 * 
		 * @param minimum
		 *            the minimum value
		 * @param maximum
		 *            the maximum value
		 */
		public DoubleRangeValidator(double minimum, double maximum)
		{
			this.minimum = minimum;
			this.maximum = maximum;

		}

		/**
		 * @see AbstractValidator#variablesMap(IValidatable)
		 */
		@Override
		protected Map<String, Object> variablesMap(IValidatable validatable)
		{
			final Map<String, Object> map = super.variablesMap(validatable);
			map.put("minimum", new Double(minimum));
			map.put("maximum", new Double(maximum));
			return map;
		}

		/**
		 * @see AbstractValidator#resourceKey(FormComponent)
		 */
		@Override
		protected String resourceKey()
		{
			return "RangeValidator";
		}

		/**
		 * @see AbstractValidator#onValidate(IValidatable)
		 */
		@Override
		protected void onValidate(IValidatable validatable)
		{
			Number value = (Number)validatable.getValue();
			if (value.doubleValue() < minimum || value.doubleValue() > maximum)
			{
				error(validatable);
			}
		}

		/**
		 * Gets the minimum.
		 * 
		 * @return minimum
		 */
		public double getMinimum()
		{
			return minimum;
		}

		/**
		 * Gets the maximum.
		 * 
		 * @return maximum
		 */
		public double getMaximum()
		{
			return maximum;
		}
	}

	/**
	 * Validator for checking if a given <code>double</code> meets the minimum requirement.
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.MinimumValidator}
	 */
	@Deprecated
	public static class DoubleMinimumValidator extends NumberValidator
	{
		private static final long serialVersionUID = 1L;
		private final double minimum;

		/**
		 * Constructor that sets the minimum value.
		 * 
		 * @param minimum
		 *            the minimum value
		 */
		public DoubleMinimumValidator(double minimum)
		{
			this.minimum = minimum;
		}

		/**
		 * @see AbstractValidator#variablesMap(IValidatable)
		 */
		@Override
		protected Map<String, Object> variablesMap(IValidatable validatable)
		{
			final Map<String, Object> map = super.variablesMap(validatable);
			if (Math.abs(minimum) == Double.MIN_VALUE)
			{
				map.put("minimum", new Integer(0));
			}
			else
			{
				map.put("minimum", new Double(minimum));
			}
			return map;
		}

		/**
		 * @see AbstractValidator#resourceKey(FormComponent)
		 */
		@Override
		protected String resourceKey()
		{
			return "MinimumValidator";
		}

		/**
		 * @see AbstractValidator#onValidate(IValidatable)
		 */
		@Override
		protected void onValidate(IValidatable validatable)
		{
			if (((Number)validatable.getValue()).doubleValue() < minimum)
			{
				error(validatable);
			}
		}

		/**
		 * Gets the minimum.
		 * 
		 * @return minimum
		 */
		public double getMinimum()
		{
			return minimum;
		}
	}

	/**
	 * Validator for checking if a given <code>double</code> meets a maximum requirement.
	 * 
	 * @deprecated see {@link org.apache.wicket.validation.validator.MaximumValidator}
	 */
	@Deprecated
	public static class DoubleMaximumValidator extends NumberValidator
	{
		private static final long serialVersionUID = 1L;
		private final double maximum;

		/**
		 * Constructor that sets the maximum value.
		 * 
		 * @param maximum
		 *            the maximum value
		 */
		public DoubleMaximumValidator(double maximum)
		{
			this.maximum = maximum;
		}

		/**
		 * @see AbstractValidator#variablesMap(IValidatable)
		 */
		@Override
		protected Map<String, Object> variablesMap(IValidatable validatable)
		{
			final Map<String, Object> map = super.variablesMap(validatable);
			if (Math.abs(maximum) == Double.MIN_VALUE)
			{
				map.put("maximum", new Integer(0));
			}
			else
			{
				map.put("maximum", new Double(maximum));
			}
			return map;
		}

		/**
		 * @see AbstractValidator#resourceKey(FormComponent)
		 */
		@Override
		protected String resourceKey()
		{
			return "MaximumValidator";
		}

		/**
		 * @see AbstractValidator#onValidate(IValidatable)
		 */
		@Override
		protected void onValidate(IValidatable validatable)
		{
			if (((Number)validatable.getValue()).doubleValue() > maximum)
			{
				error(validatable);
			}
		}

		/**
		 * Gets the maximum.
		 * 
		 * @return maximum
		 */
		public double getMaximum()
		{
			return maximum;
		}
	}
}
