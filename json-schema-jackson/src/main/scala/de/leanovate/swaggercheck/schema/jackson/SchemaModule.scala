package de.leanovate.swaggercheck.schema.jackson

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.Module.SetupContext
import de.leanovate.swaggercheck.schema.model.Definition



object SchemaModule extends Module {
  override def getModuleName: String = "SchemaModule"

  override def version(): Version = Version.unknownVersion()

  override def setupModule(context: SetupContext): Unit = {

    context.setMixInAnnotations(classOf[Definition], classOf[DefinitionMixin])
  }

}
