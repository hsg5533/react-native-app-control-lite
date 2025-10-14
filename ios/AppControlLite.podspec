Pod::Spec.new do |s|
  s.name         = "AppControlLite"
  s.version      = "0.1.0"
  s.summary      = "Lightweight restart/reload controls for React Native apps"
  s.license      = "MIT"
  s.authors      = { "you" => "you@example.com" }
  s.platforms    = { :ios => "12.0" }
  s.source       = { :path => "." }
  s.source_files = "AppControlLite*.{h,mm}"
  s.requires_arc = true
  s.dependency "React-Core"
end
