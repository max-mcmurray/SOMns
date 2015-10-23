package som.interpreter;

import som.compiler.MethodBuilder;
import som.compiler.Variable.Local;
import som.interpreter.LexicalScope.MethodScope;
import som.interpreter.nodes.ExpressionNode;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;

public abstract class Invokable extends RootNode {

  @Child protected ExpressionNode  expressionOrSequence;

  protected final ExpressionNode uninitializedBody;

  public Invokable(final SourceSection sourceSection,
      final FrameDescriptor frameDescriptor,
      final ExpressionNode expressionOrSequence,
      final ExpressionNode uninitialized) {
    super(SomLanguage.class, sourceSection, frameDescriptor);
    this.uninitializedBody    = uninitialized;
    this.expressionOrSequence = expressionOrSequence;
  }

  @Override
  public final Object execute(final VirtualFrame frame) {
    return expressionOrSequence.executeGeneric(frame);
  }

  public abstract Invokable cloneWithNewLexicalContext(final MethodScope outerContext);

  public ExpressionNode inline(final MethodBuilder builder,
      final Local[] locals) {
    return InlinerForLexicallyEmbeddedMethods.doInline(uninitializedBody,
        builder, locals, getSourceSection().getCharIndex());
  }

  @Override
  public final boolean isCloningAllowed() {
    return true;
  }

  public final RootCallTarget createCallTarget() {
    return Truffle.getRuntime().createCallTarget(this);
  }

  public abstract void propagateLoopCountThroughoutMethodScope(final long count);
}
