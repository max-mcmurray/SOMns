package som.interpreter.nodes.specialized;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.Tag;

import bd.tools.nodes.Operation;
import som.compiler.AccessModifier;
import som.interpreter.nodes.dispatch.AbstractDispatchNode;
import som.interpreter.nodes.dispatch.UninitializedDispatchNode;
import som.interpreter.nodes.nary.BinaryBasicOperation;
import som.vm.Symbols;
import som.vmobjects.SObjectWithClass;
import tools.dym.Tags.OpComparison;


@GenerateNodeFactory
public abstract class AndBoolMessageNode extends BinaryBasicOperation
    implements Operation {
  @Override
  protected boolean hasTagIgnoringEagerness(final Class<? extends Tag> tag) {
    if (tag == OpComparison.class) {
      return true;
    } else {
      return super.hasTagIgnoringEagerness(tag);
    }
  }

  @Specialization
  public final boolean doAnd(final VirtualFrame frame, final boolean receiver,
      final boolean argument) {
    return receiver && argument;
  }

  protected AbstractDispatchNode createDispatch() {
    return UninitializedDispatchNode.createRcvrSend(null, Symbols.symbolFor(getOperation()), AccessModifier.PROTECTED);
  }

  @Specialization
  public final Object normalMessageSend(VirtualFrame frame, SObjectWithClass rcvr, Object other,
      @Cached("createDispatch()") AbstractDispatchNode dispatch) {
    return dispatch.executeDispatch(frame, new Object[] {rcvr, other});
  }

  @Override
  public String getOperation() {
    return "&&";
  }

  @Override
  public int getNumArguments() {
    return 2;
  }
}
