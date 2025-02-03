package omaloon.annotations.lombok.autoimpl.info;

import asmlib.lombok.javaparser.CompileBodyVisitor;
import com.sun.tools.javac.tree.JCTree;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.LombokNode;
import lombok.experimental.FieldDefaults;
import lombok.javac.JavacNode;
import omaloon.annotations.AutoImplement;
import one.util.streamex.StreamEx;

import java.util.Map;
import java.util.function.Function;

public class AutoImplInformation {
    public Map<String, MethodInfo> methods;
    public Map<String, FieldInfo> fields;

    public static AutoImplInformation build(AnnotationValues<AutoImplement> annotationValues, JCTree.JCAnnotation jcAnnotation, JavacNode annotationNode) {
        AutoImplInformation information = new AutoImplInformation();
        JavacNode typeNode = annotationNode.up();
        CompileBodyVisitor transformer = new CompileBodyVisitor(typeNode.getTreeMaker(), typeNode.getAst(), typeNode.getContext());

        information.methods = StreamEx
            .of(typeNode.down().iterator())
            .filterBy(LombokNode::getKind, AST.Kind.METHOD)
            .map(javacNode -> {
                AnnotationValues<AutoImplement.Inject> foundAnnotation = javacNode.findAnnotation(AutoImplement.Inject.class);
                AutoImplement.Inject annotationInstance = foundAnnotation == null ? null : foundAnnotation.getInstance();
                return MethodInfo.create(
                    typeNode,
                    javacNode,
                    annotationInstance
                );
            })
            .mapToEntry(it -> it.info.descriptor, Function.identity())
            .toMap()
        ;


        information.fields = StreamEx
            .of(typeNode.down().iterator())
            .filterBy(LombokNode::getKind, AST.Kind.FIELD)
            .mapToEntry(JavacNode::getName, Function.identity())
            .mapValues(javacNode -> new FieldInfo(
                typeNode.getName(),
                typeNode,
                javacNode.getName(),
                cast(javacNode),
                javacNode,
                transformer
            ))
            .toMap();
        return information;
    }

    private static <T extends JCTree> T cast(JavacNode javacNode) {
        return (T) javacNode.get();
    }

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
    public static class FieldInfo {
        String intefaceName;
        JavacNode typeNode;
        String name;
        @NonNull
        JCTree.JCVariableDecl decl;
        @NonNull
        JavacNode fieldNode;
        CompileBodyVisitor transformer;
    }
}
