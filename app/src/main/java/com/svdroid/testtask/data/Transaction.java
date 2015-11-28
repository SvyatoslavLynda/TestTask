package com.svdroid.testtask.data;

public class Transaction
{
	public int id;
	public double amount;
	public String currency;
	public String sku;

	@Override
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Transaction that = (Transaction) o;

		if (id != that.id) {
			return false;
		}
		if (Double.compare(that.amount, amount) != 0) {
			return false;
		}
		if (currency != null ? !currency.equals(that.currency) : that.currency != null) {
			return false;
		}
		return !(sku != null ? !sku.equals(that.sku) : that.sku != null);

	}

	@Override
	public int hashCode()
	{
		int result;
		long temp;
		result = id;
		temp = Double.doubleToLongBits(amount);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (currency != null ? currency.hashCode() : 0);
		result = 31 * result + (sku != null ? sku.hashCode() : 0);
		return result;
	}
}
