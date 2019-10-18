require_relative 'grid/grid'
require_relative 'algos/sidewinder'
require_relative 'seed'

seed = Seed.set(ENV['SEED'])

grid = Grid.new(4,4)
SideWinder.on(grid)

puts "Complete."
puts grid

puts "Seed: #{seed}"