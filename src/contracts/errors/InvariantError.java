package contracts.errors;

public class InvariantError extends ContractError {
	private static final long serialVersionUID = 2050146913131637445L;

	public InvariantError(String message) {
		super("Invariant failed: "+message);
	}
}
