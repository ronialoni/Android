package il.ac.tau.team3.spcomm;

public interface ICommHandler<T> {
	
	public void onRecv(T Obj);
	public void onTimeout(T Obj);
	public void onError(T Obj);
}
