These are the protobuf definition files used by core hbase. This modules
does shading of all to do with protobuf. All of core relies on this module.
All core references in core to protobuf are to the protobuf this module
includes but offset by the package prefix of org.apache.hadoop.hbase.shaded.*
as in org.apache.hadoop.hbase.shaded.protobuf.generated.* and
org.apache.hadoop.hbase.shaded.com.google.protobuf.*.

NOTE: the .protos in here are copied in an adjacent module, hbase-protocol.
There they are non-shaded. If you make changes here, consider making them
over in the adjacent module too. Be careful, the .proto files are not
exactly the same; they differ in one line at least -- the location the file
gets generated to; i.e. those in here get the extra 'shaded' in their
package name.

The produced java classes are generated and then checked in. The reasoning
is that they change infrequently.

To regenerate the classes after making definition file changes, ensure first that
the protobuf protoc tool is in your $PATH. You may need to download it and build
it first; its part of the protobuf package. For example, if using v2.5.0 of
protobuf, it is obtainable from here:

 https://github.com/google/protobuf/releases/tag/v2.5.0

HBase uses hadoop-maven-plugins:protoc goal to invoke the protoc command. You can
compile the protoc definitions by invoking maven with profile compile-protobuf or
passing in compile-protobuf property.

mvn compile -Dcompile-protobuf
or
mvn compile -Pcompile-protobuf

You may also want to define protoc.path for the protoc binary

mvn compile -Dcompile-protobuf -Dprotoc.path=/opt/local/bin/protoc

If you have added a new proto file, you should add it to the pom.xml file first.
Other modules also support the maven profile.

After you've done the above, check it in and then check it in (or post a patch
on a JIRA with your definition file changes and the generated files).
