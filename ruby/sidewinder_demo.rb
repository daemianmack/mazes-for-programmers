require_relative 'grid/grid'
require_relative 'algos/sidewinder_a'
require_relative 'seed'

seed = Seed.set(ENV['SEED'])
puts "Seed: #{seed}"

grid = Grid.new(4,4)
SideWinderA.on(grid, ENV['DEBUG'])

puts "Complete."
puts grid
